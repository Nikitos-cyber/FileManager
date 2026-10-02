
import java.io.IOException;
import java.util.Scanner;
import java.util.Locale;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileReader;

public class Manager {
    private String path;
    Scanner scanner;

    Manager() {
        path = System.getProperty("user.home");
        scanner = new Scanner(System.in);
    }

    public void getLog() throws IOException {
        String path = System.getProperty("user.home") + "\\log\\app.log";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Отсутсвует файл логов");
            return;
        }
        System.out.println("\n\nSTART-------------------READ-------------------LOG\n");
        FileReader reader = new FileReader(path);
        int ch;
        while ((ch = reader.read()) != -1) {
            System.out.print((char) ch);
        }
        System.out.println("\n\nEND----------------------OF-------------------FILE\n\n");
    }


    public void log(String error) throws IOException {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MMMM.yyyy HH:mm:ss");
        String path = System.getProperty("user.home") + "\\log\\app.log";
        File file = new File(path);
        if (!file.getParentFile().mkdirs()) {
            System.out.println("Ошибка лога!");
            return;
        }

        FileWriter writer = new FileWriter(file);
        writer.write(date.format(format));
        writer.write("\n");
        writer.write(error);
        writer.close();
    }


    public void menu() throws IOException {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        String menu =
                """
                        Меню:
                        1.Показать корневой каталог;
                        2.Создать каталог;
                        3.Создать файл;
                        4.Удалить каталог;
                        5.Удалить файл;
                        6.Посмотреть логи;
                        7.Сменить дерикторию;
                        8.Показать все файлы директории;
                        9.Выход;
                        
                        Введите число пункта меню:
                        """;

        while (true) {
            System.out.printf(menu);

            while (!scanner.hasNextInt()) {
                scanner.next();
            }

            int item = scanner.nextInt();

            switch (item) {
                case 1:
                    System.out.println(pwd());
                    break;
                case 2:
                    createDirectory();
                    break;
                case 3:
                    createFile();
                    break;
                case 4:
                    deleteDir();
                    break;
                case 5:
                    deleteFile();
                    break;
                case 6:
                    getLog();
                    break;
                case 7:
                    changeDir();
                    break;
                case 8:
                    showDir();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Нет такого пункта меню!");
            }
        }
    }


    public String pwd() {
        return path;
    }

    public boolean isExistsPath(String path) {
        File checkPath = new File(parse(path));
        return checkPath.exists();
    }

    public String parse(String path) {
        return path = path.replace("/", "\\");
    }


    public boolean isCorrectPath(String path) {
        path = parse(path);
        return path.charAt(0) == 'C' || path.charAt(0) == 'D'
                &&
                path.charAt(1) == ':'
                ||
                path.charAt(1) == '\\' && path.charAt(2) == '\\';
    }

    public void changeDir() throws IOException {
        String path = initPath();
        if (isExistsPath(path))
            this.path = path;
        else {
            System.out.println("Директории не существует...");
            log("Директории не существует");
        }
    }

    public void showDir() throws IOException {
        String path = initPath();
        if (!isExistsPath(path)) {
            System.out.println("Директори не существует...");
            log("Директори не существует...");
        }
        File file = new File(path);
        File[] list = file.listFiles();

        if (list == null)
            return;

        for (File item : list) {
            if (item.isDirectory())
                System.out.println("/" + item.getName());
            else
                System.out.println(item);
        }
    }


    public String initPath() {

        System.out.println("Введите путь: ");
        String path = scanner.next();

        while (!isCorrectPath(path)) {
            path = scanner.next();
        }
        return path;
    }

    public boolean messageCreated(String str, String path) {
        if (isExistsPath(path)) {
            System.out.println(str);
            return true;
        }
        return false;
    }

    public void createDirectory() throws IOException {

        String path = initPath();

        if (messageCreated("Каталог существует!", path))
            return;

        File dir = new File(path);

        if (!dir.mkdir()) {
            System.out.println("Ошибка создания каталога!");
            log("Ошибка создания каталога!");
        } else
            System.out.println("Каталог создан!");

    }

    public void createFile() throws IOException {

        String path = initPath();

        if (messageCreated("Файл существует", path))
            return;

        File file = new File(path);

        if (file.createNewFile())
            System.out.println("Файл создан!");
        else {
            System.out.println("Ошибка создания файла");
            log("Ошибка создания файла");
        }

    }

    public boolean delete(String path) {
        File file = new File(path);
        return file.delete();
    }

    public void recursiveDelete(String path) throws IOException {
        File file = new File(path);
        File[] list = file.listFiles();

        if (list == null)
            return;

        for (File item : list) {
            if (item.isFile()) {
                if (!delete(item.getAbsolutePath())) {
                    System.out.println("Ошибка удаления файла...");
                    log("Ошибка удаления файла...");
                } else
                    System.out.println("Файл удален: " + item.getName());
            } else
                recursiveDelete(item.getAbsolutePath());
        }

        if (!delete(path)) {
            System.out.println("Ошибка удаления каталога...");
            log("Ошибка удаления каталога...");
        } else
            System.out.println("Каталог удален");
    }


    public void deleteDir() throws IOException {
        String path = initPath();
        if (!messageCreated("", path))
            return;

        recursiveDelete(path);

    }

    public void deleteFile() throws IOException {
        String path = initPath();

        if (!messageCreated("", path))
            return;

        if (delete(path))
            System.out.println("Файл удален!");
        else {
            System.out.println("Ошибка удаления файла...");
            log("Ошибка удаления файла...");
        }
    }
}
