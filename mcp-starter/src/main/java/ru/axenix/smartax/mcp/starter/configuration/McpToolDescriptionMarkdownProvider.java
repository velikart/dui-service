package ru.axenix.smartax.mcp.starter.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import ru.axenix.smartax.mcp.starter.annotation.ToolDescriptionFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class McpToolDescriptionMarkdownProvider implements McpToolDescriptionProvider {

    private static final String MARKDOWN_EXTENSION = ".md";
    private static final String CLASSPATH_PREFIX = "classpath:";

    private final SmartaxMcpToolProperties properties;

    @Override
    public Optional<String> resolveDescription(Tool tool, Method method) {
        String descriptionFileName = resolveDescriptionFileName(tool, method);
        String basePath = properties.getDescriptionPath();

        Optional<String> description = readFromFileSystem(basePath, descriptionFileName);
        if (description.isPresent()) {
            log.info("MCP description loaded from file system for tool: {}", tool.name());
        } else {
            description = readFromClasspath(basePath, descriptionFileName);
            if (description.isPresent()) {
                log.info("MCP description loaded from classpath for tool: {}", tool.name());
            } else {
                log.warn("MCP description file not found for tool: {} (basePath='{}', file='{}'). Fallback to @Tool(description).",
                        tool.name(), basePath, descriptionFileName);
            }
        }

        return description;
    }

    private Optional<String> readFromFileSystem(String basePath, String descriptionFileName) {
        Optional<String> description = Optional.empty();
        Path root = resolvePath(basePath);
        if (root != null) {
            Path markdownFile = root.resolve(descriptionFileName).normalize();
            if (markdownFile.startsWith(root) && Files.isRegularFile(markdownFile)) {
                try {
                    String markdown = Files.readString(markdownFile, StandardCharsets.UTF_8).trim();
                    if (StringUtils.hasText(markdown)) {
                        description = Optional.of(markdown);
                    }
                } catch (IOException e) {
                    log.warn("Unable to read MCP markdown description file from file system: {}", markdownFile, e);
                }
            }
        }

        return description;
    }

    private Path resolvePath(String basePath) {
        try {
            return Path.of(basePath).normalize();
        } catch (Exception e) {
            return null;
        }
    }

    private Optional<String> readFromClasspath(String basePath, String descriptionFileName) {
        String normalizedBasePath = basePath.startsWith(CLASSPATH_PREFIX)
                ? basePath.substring(CLASSPATH_PREFIX.length())
                : basePath;
        String normalizedPath = normalizedBasePath.replaceAll("^/+", "");
        String resourcePath = normalizedPath + "/" + descriptionFileName;

        ClassPathResource classPathResource = new ClassPathResource(resourcePath);
        if (!classPathResource.exists()) {
            return Optional.empty();
        }

        try {
            String markdown = new String(classPathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            return StringUtils.hasText(markdown) ? Optional.of(markdown) : Optional.empty();
        } catch (IOException e) {
            log.warn("Unable to read MCP markdown description file from classpath: {}", resourcePath, e);
            return Optional.empty();
        }
    }

    private String resolveDescriptionFileName(Tool tool, Method method) {
        ToolDescriptionFile override = method.getAnnotation(ToolDescriptionFile.class);
        if (override != null && StringUtils.hasText(override.value())) {
            String value = override.value().trim();
            return value.endsWith(MARKDOWN_EXTENSION) ? value : value + MARKDOWN_EXTENSION;
        }

        String toolName = StringUtils.hasText(tool.name()) ? tool.name() : method.getName();
        return toolName + MARKDOWN_EXTENSION;
    }
}
