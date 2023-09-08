package tmp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Practicum {

    private static final String HOME = System.getProperty("user.home");

    public static void main(String[] args) throws IOException {
        byte b = -127;
        char c = (char) b;
        for (int i = -128; i < 128; i++) {
            byte x = (byte) i;
            char y = (char) x;
            System.out.println("byte: " + x + " char: " + y);
        }


        // создание файла testFile
        Path testFile =
                Files.createFile(Paths.get(HOME, "testFile.txt"));

        if (Files.exists(Paths.get(HOME, "testFile.txt"))) {
            System.out.println("Файл успешно создан.");
        }

        // создание директории testDirectory
        Path testDirectory = Files.createDirectory(Paths.get(HOME,
                "testDirectory"));
        if (Files.exists(Paths.get(HOME, "testDirectory"))) {
            System.out.println("Директория успешно создана.");
        }

        // перемещение файла testFile в директорию testDirectory
        testFile = Files.move(testFile,
                Paths.get(HOME, "testDirectory", "testFile.txt"),
                REPLACE_EXISTING);

        if (Files.exists(
                Paths.get(HOME, "testDirectory", "testFile.txt"))) {

            System.out.println("Файл перемещён в testDirectory.");
        }

        // удаление файла
        Files.delete(testFile);
        if (!Files.exists(
                Paths.get(HOME, "testDirectory", "testFile.txt"))) {

            System.out.println("Тестовый файл удалён.");
        }

        // удаление пустой директории
        Files.delete(testDirectory);
        if (!Files.exists(Paths.get(HOME, "testDirectory"))) {
            System.out.println("Директория удалена.");
        }
    }
}
