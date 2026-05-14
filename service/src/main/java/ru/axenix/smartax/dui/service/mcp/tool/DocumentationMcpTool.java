package ru.axenix.smartax.dui.service.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@DuiMcpTool
public class DocumentationMcpTool {

    private static final String CLASSPATH_PREFIX = "classpath:";

    @Value("${dui.mcp.documentation-path:docs/instruction}")
    private String documentationPath;

    @Tool(name = "listDocumentationFiles", description = "List files from dui/instruction folder")
    public List<String> listDocumentationFiles() {
        List<String> fileSystemFiles = listFromFileSystem();
        if (!fileSystemFiles.isEmpty()) {
            return fileSystemFiles;
        }

        List<String> classpathFiles = listFromClasspath();
        if (!classpathFiles.isEmpty()) {
            return classpathFiles;
        }

        throw new IllegalStateException("Папка документации не найдена: " + documentationPath);
    }

    @Tool(name = "getDocumentationFile", description = "Read a file from dui/instruction folder by fileName")
    public String getDocumentationFile(
            @ToolParam(description = "Filename from dui/instruction folder") String fileName
    ) {
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("fileName не должен быть пустым");
        }

        String normalizedFileName = fileName.trim();
        if (normalizedFileName.contains("..") || normalizedFileName.contains("/") || normalizedFileName.contains("\\\\")) {
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
            return java.util.Arrays.stream(resources)
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
            throw new IllegalStateException("Не удалось прочитать файл: " + fileName, e);
        }
    }

    private String readFromClasspath(String fileName) {
        String classpathRoot = normalizeClasspathRoot();
        String resourcePath = classpathRoot + "/" + fileName;
        ClassPathResource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            return null;
        }

        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать файл: " + fileName, e);
        }
    }

    private String normalizeClasspathRoot() {
        String normalized = documentationPath.startsWith(CLASSPATH_PREFIX)
                ? documentationPath.substring(CLASSPATH_PREFIX.length())
                : documentationPath;
        return normalized.replaceAll("^/+", "");
    }
}
