package ru.axenix.smartax.dui.service.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@DuiMcpTool
public class DocumentationMcpTool {

    @Value("${dui.mcp.documentation-path:docs/instruction}")
    private String documentationPath;

    @Tool(name = "listDocumentationFiles", description = "List files from dui/instruction folder")
    public List<String> listDocumentationFiles() {
        Path root = Path.of(documentationPath).normalize();
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException("Папка документации не найдена: " + root.toAbsolutePath());
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

    @Tool(name = "getDocumentationFile", description = "Read a file from dui/instruction folder by fileName")
    public String getDocumentationFile(
            @ToolParam(description = "Filename from dui/instruction folder") String fileName
    ) {
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("fileName не должен быть пустым");
        }

        Path root = Path.of(documentationPath).normalize();
        Path filePath = root.resolve(fileName).normalize();

        if (!filePath.startsWith(root)) {
            throw new IllegalArgumentException("Недопустимое имя файла");
        }
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("Файл не найден: " + fileName);
        }

        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать файл: " + fileName, e);
        }
    }
}