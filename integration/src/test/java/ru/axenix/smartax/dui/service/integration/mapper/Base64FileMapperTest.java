package ru.axenix.smartax.dui.service.integration.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для {@link Base64FileMapper}.
 */
class Base64FileMapperTest {

    @TempDir
    private Path tempDir;

    private Base64FileMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new Base64FileMapper(tempDir.toString());
    }

    @Test
    void mapShouldCreateFileWithDecodedContent() throws IOException {
        String content = "Тестовое содержимое";
        byte[] originalBytes = content.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.getEncoder().encodeToString(originalBytes);

        File file = mapper.map(base64, "test.txt");

        assertNotNull(file, "Файл не должен быть null");
        assertTrue(file.exists(), "Файл должен существовать");
        assertTrue(file.toPath().startsWith(tempDir),
                "Файл должен создаваться в tempDir теста");

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        assertArrayEquals(originalBytes, fileBytes,
                "Содержимое файла должно совпадать с исходным");
    }

    @Test
    void mapShouldUseDefaultNameWhenOriginalIsNull() throws IOException {
        String content = "data";
        String base64 = Base64.getEncoder()
                .encodeToString(content.getBytes(StandardCharsets.UTF_8));

        File file = mapper.map(base64, null);

        assertNotNull(file, "Файл не должен быть null");
        String fileName = file.getName();
        assertTrue(fileName.contains("_file"),
                "Имя файла должно содержать суффикс по умолчанию");
    }

    @Test
    void mapShouldNormalizeFileName() throws IOException {
        String content = "data";
        String base64 = Base64.getEncoder()
                .encodeToString(content.getBytes(StandardCharsets.UTF_8));

        String originalName = "inv@lid имя.txt";
        File file = mapper.map(base64, originalName);

        String fileName = file.getName();

        assertTrue(!fileName.contains(" ") && !fileName.contains("@"),
                "Имя файла не должно содержать недопустимые символы");
        assertTrue(fileName.endsWith(".txt"),
                "Расширение .txt должно сохраниться после нормализации");
    }
}
