package ru.axenix.smartax.dui.service.integration.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Mapper для преобразования содержимого файла,
 * представленного в виде Base64-строки,
 * во временный файл {@link File}.
 *
 * Маппер обеспечивает:
 * <ul>
 *     <li>нормализацию имени файла;</li>
 *     <li>создание безопасной приватной временной директории;</li>
 *     <li>создание временного файла с безопасным именем;</li>
 *     <li>запись бинарного содержимого на диск;</li>
 * </ul>
 *
 * Базовый каталог задаётся через свойство {@code app.files.temp-dir}.
 * По умолчанию используется {@code systemProperties['user.home']}.
 *
 * Используется там, где необходимо конвертировать
 * файл, полученный от внешних систем, в локальный временный объект {@link File}.
 *
 * @author Artem Velikanov.
 */
@Component
public class Base64FileMapper {

    /** Значение по умолчанию для имени файла. */
    private static final String DEFAULT_FILE_NAME = "file";

    /** Символ подчёркивания, используемый и как замена, и как разделитель. */
    private static final String UNDERSCORE = "_";

    /** Строка регулярного выражения для определения недопустимых символов имени файла. */
    private static final String SAFE_NAME_REGEX = "[^a-zA-Z0-9._-]";

    /** Название приватной директории приложения в baseDir. */
    private static final String TEMP_DIR_NAME = ".appfiles";

    /** Подкаталог временных файлов. */
    private static final String TEMP_SUBDIR_NAME = "tmp";

    /** Префикс имени временного файла. */
    private static final String FILE_PREFIX = "file_";

    /** Базовый каталог для временных файлов. */
    private final Path baseTempDir;

    /**
     * Конструктор для использования Spring.
     * <p>
     * Если свойство {@code app.files.temp-dir} не указано, используется {@code user.home}.
     *
     * @param baseDir базовый каталог, внутри которого создаются временные файлы.
     */
    public Base64FileMapper(
            @Value("${app.files.temp-dir:#{systemProperties['user.home']}}")
            String baseDir
    ) {
        this.baseTempDir = Paths.get(baseDir, TEMP_DIR_NAME, TEMP_SUBDIR_NAME);
    }

    /**
     * Преобразует содержимое в Base64 в локальный временный файл.
     *
     * @param base64Content строка Base64 с бинарным содержимым файла.
     * @param originalFileName исходное имя файла, полученное от внешней системы.
     * @return временный файл {@link File}, содержащий декодированные данные.
     * @throws IOException если произошла ошибка при создании каталога или записи файла.
     */
    public File map(String base64Content, String originalFileName) throws IOException {
        byte[] data = Base64.getDecoder().decode(base64Content);

        String safeName = normalizeFileName(originalFileName);

        Path tempDir = createPrivateTempDirectory();

        Path tempFile = Files.createTempFile(
                tempDir,
                FILE_PREFIX,
                UNDERSCORE + safeName
        );

        Files.write(tempFile, data);

        return tempFile.toFile();
    }

    /**
     * Нормализует имя файла, заменяя недопустимые символы на {@link #UNDERSCORE}.
     *
     * @param fileName исходное имя файла.
     * @return безопасное имя файла.
     */
    private String normalizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return DEFAULT_FILE_NAME;
        }
        return fileName.replaceAll(SAFE_NAME_REGEX, UNDERSCORE);
    }

    /**
     * Создаёт приватную временную директорию внутри {@link #baseTempDir}.
     * <p>
     * Директория используется для хранения временных файлов приложения.
     *
     * @return путь к приватной временной директории.
     * @throws IOException если директорию невозможно создать.
     */
    private Path createPrivateTempDirectory() throws IOException {
        Files.createDirectories(baseTempDir);
        return baseTempDir;
    }
}
