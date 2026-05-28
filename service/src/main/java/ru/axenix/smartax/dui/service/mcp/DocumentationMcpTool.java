package ru.axenix.smartax.dui.service.mcp;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;
import ru.axenix.smartax.lib.mcp.annotation.SmartaxMcpTool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * MCP-инструменты для чтения документации.
 */
@SmartaxMcpTool
public class DocumentationMcpTool {

    private static final String CLASSPATH_PREFIX = "classpath:";
    private static final String PATH_SEPARATOR = "/";
    private static final String READ_FILE_ERROR_MESSAGE = "Не удалось прочитать файл: ";

    @Value("${dui.mcp.documentation-path:instruction}")
    private String documentationPath;

    /**
     * Получение списка файлов документации.
     *
     * @return Список названий файлов документации.
     */
    @Tool(name = "listDocumentationFiles", description = "List files from dui/instruction folder")
    public List<String> listDocumentationFiles() {
        List<String> fileSystemFiles = listFromFileSystem();
        if (CollectionUtils.isNotEmpty(fileSystemFiles)) {
            return fileSystemFiles;
        }

        List<String> classpathFiles = listFromClasspath();
        if (CollectionUtils.isNotEmpty(classpathFiles)) {
            return classpathFiles;
        }

        throw new IllegalStateException("Папка документации не найдена: " + documentationPath);
    }

    /**
     * Получение контекта документации конкретного файла
     *
     * @param fileName название файла документации
     * @return Текст документации.
     */
    @Tool(name = "getDocumentationFile", description = "Read a file from dui/instruction folder by fileName")
    public String getDocumentationFile(
        @ToolParam(description = "Filename from dui/instruction folder") String fileName
    ) {
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("fileName не должен быть пустым");
        }

        String normalizedFileName = fileName.trim();
        if (normalizedFileName.contains("..") || normalizedFileName.contains(PATH_SEPARATOR) || normalizedFileName.contains("\\\\")) {
            throw new IllegalArgumentException("Недопустимое имя файла");
        }

        String fileSystemContent = readFromFileSystem(normalizedFileName);
        if (fileSystemContent != null) {
            return fileSystemContent;
        }

        String classpathContent = readFromClasspath(normalizedFileName);
        if (classpathContent != null) {
            return classpathContent;
        }

        throw new IllegalArgumentException("Файл не найден: " + normalizedFileName);
    }

    private List<String> listFromFileSystem() {
        Path root;
        try {
            root = Path.of(documentationPath).normalize();
        } catch (Exception e) {
            return List.of();
        }

        if (!Files.isDirectory(root)) {
            return List.of();
        }

        try (var stream = Files.list(root)) {
            return stream
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .sorted()
                .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать папку документации", e);
        }
    }

    private List<String> listFromClasspath() {
        String classpathRoot = normalizeClasspathRoot();
        String searchPattern = CLASSPATH_PREFIX + classpathRoot + "/*";

        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(searchPattern);
            return Arrays.stream(resources)
                .filter(Resource::isReadable)
                .map(Resource::getFilename)
                .filter(StringUtils::hasText)
                .sorted(Comparator.naturalOrder())
                .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private String readFromFileSystem(String fileName) {
        Path root;
        try {
            root = Path.of(documentationPath).normalize();
        } catch (Exception e) {
            return null;
        }

        Path filePath = root.resolve(fileName).normalize();
        if (!filePath.startsWith(root) || !Files.isRegularFile(filePath)) {
            return null;
        }

        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE + fileName, e);
        }
    }

    private String readFromClasspath(String fileName) {
        String classpathRoot = normalizeClasspathRoot();
        String resourcePath = classpathRoot + PATH_SEPARATOR + fileName;
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            return null;
        }

        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE + fileName, e);
        }
    }

    private String normalizeClasspathRoot() {
        String normalized = documentationPath.startsWith(CLASSPATH_PREFIX)
            ? documentationPath.substring(CLASSPATH_PREFIX.length())
            : documentationPath;
        return normalized.replaceAll("^/+", "");
    }
}