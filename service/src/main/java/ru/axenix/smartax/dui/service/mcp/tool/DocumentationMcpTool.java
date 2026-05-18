package ru.axenix.smartax.dui.service.mcp.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import ru.axenix.smartax.common.exception.SmartaxException;
import ru.axenix.smartax.common.model.error.SmartaxError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * MCP-инструменты для чтения документации.
 */
@DuiMcpTool
public class DocumentationMcpTool {

    @Value("${dui.mcp.documentation-path:docs/instruction}")
    private String documentationPath;

    /**
     * Возвращает список файлов документации в корневой папке документации.
     *
     * @return отсортированный список имен файлов
     */
    @Tool(name = "listDocumentationFiles", description = "List files from dui/instruction folder")
    public List<String> listDocumentationFiles() {
        Path root = Path.of(documentationPath).normalize();
        if (!Files.isDirectory(root)) {
            throw new SmartaxException("Папка документации не найдена: " + root.toAbsolutePath(), SmartaxError.BAD_REQUEST);
        }

        try (var stream = Files.list(root)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new SmartaxException("Не удалось прочитать папку документации", SmartaxError.BAD_REQUEST, e);
        }
    }

    /**
     * Возвращает содержимое файла документации по его имени.
     *
     * @param fileName имя файла из папки документации
     * @return текстовое содержимое файла
     */
    @Tool(name = "getDocumentationFile", description = "Read a file from dui/instruction folder by fileName")
    public String getDocumentationFile(
            @ToolParam(description = "Filename from dui/instruction folder") String fileName
    ) {
        if (!StringUtils.hasText(fileName)) {
            throw new SmartaxException("fileName не должен быть пустым", SmartaxError.BAD_REQUEST);
        }

        Path root = Path.of(documentationPath).normalize();
        Path filePath = root.resolve(fileName).normalize();

        if (!filePath.startsWith(root)) {
            throw new SmartaxException("Недопустимое имя файла", SmartaxError.BAD_REQUEST);
        }
        if (!Files.isRegularFile(filePath)) {
            throw new SmartaxException("Файл не найден: " + fileName, SmartaxError.BAD_REQUEST);
        }

        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SmartaxException("Не удалось прочитать файл: " + fileName, SmartaxError.BAD_REQUEST, e);
        }
    }
}