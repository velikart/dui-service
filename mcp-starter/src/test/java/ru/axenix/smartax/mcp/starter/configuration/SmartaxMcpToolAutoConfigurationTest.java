package ru.axenix.smartax.mcp.starter.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.ApplicationContext;
import ru.axenix.smartax.mcp.starter.annotation.SmartaxMcpTool;
import ru.axenix.smartax.mcp.starter.annotation.ToolDescriptionFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SmartaxMcpToolAutoConfigurationTest {

    @Test
    void mcpTools_CreatesProviderFromAnnotatedBeans() {
        SmartaxMcpToolAutoConfiguration configuration = new SmartaxMcpToolAutoConfiguration();
        SmartaxMcpToolProperties properties = new SmartaxMcpToolProperties();
        McpToolDescriptionMarkdownProvider markdownProvider = new McpToolDescriptionMarkdownProvider(properties);
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(SmartaxMcpTool.class)).thenReturn(Map.of("dummy", new DummyMcpTool()));

        ToolCallbackProvider provider = configuration.mcpTools(context, List.of(markdownProvider));

        assertNotNull(provider);
    }

    @Test
    void mcpTools_UsesMarkdownDescriptionWhenFileExists(@TempDir Path tempDir) throws IOException {
        SmartaxMcpToolAutoConfiguration configuration = new SmartaxMcpToolAutoConfiguration();
        SmartaxMcpToolProperties properties = new SmartaxMcpToolProperties();
        properties.setDescriptionPath(tempDir.toString());
        McpToolDescriptionMarkdownProvider markdownProvider = new McpToolDescriptionMarkdownProvider(properties);

        Files.writeString(tempDir.resolve("ping.md"), "# Ping\n\nMarkdown description");

        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(SmartaxMcpTool.class)).thenReturn(Map.of("dummy", new DummyMcpTool()));

        ToolCallbackProvider provider = configuration.mcpTools(context, List.of(markdownProvider));
        ToolCallback callback = provider.getToolCallbacks()[0];

        assertEquals("# Ping\n\nMarkdown description", callback.getToolDefinition().description());
    }

    @Test
    void mcpTools_UsesAnnotationDescriptionWhenMarkdownFileMissing(@TempDir Path tempDir) {
        SmartaxMcpToolAutoConfiguration configuration = new SmartaxMcpToolAutoConfiguration();
        SmartaxMcpToolProperties properties = new SmartaxMcpToolProperties();
        properties.setDescriptionPath(tempDir.toString());
        McpToolDescriptionMarkdownProvider markdownProvider = new McpToolDescriptionMarkdownProvider(properties);

        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(SmartaxMcpTool.class)).thenReturn(Map.of("dummy", new DummyMcpTool()));

        ToolCallbackProvider provider = configuration.mcpTools(context, List.of(markdownProvider));
        ToolCallback callback = provider.getToolCallbacks()[0];

        assertEquals("ping", callback.getToolDefinition().description());
    }

    @Test
    void mcpTools_UsesOverriddenMarkdownFileName(@TempDir Path tempDir) throws IOException {
        SmartaxMcpToolAutoConfiguration configuration = new SmartaxMcpToolAutoConfiguration();
        SmartaxMcpToolProperties properties = new SmartaxMcpToolProperties();
        properties.setDescriptionPath(tempDir.toString());
        McpToolDescriptionMarkdownProvider markdownProvider = new McpToolDescriptionMarkdownProvider(properties);

        Files.writeString(tempDir.resolve("custom-doc.md"), "# Custom\n\nFrom override");

        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(SmartaxMcpTool.class)).thenReturn(Map.of("dummy", new OverrideMcpTool()));

        ToolCallbackProvider provider = configuration.mcpTools(context, List.of(markdownProvider));
        ToolCallback callback = provider.getToolCallbacks()[0];

        assertEquals("# Custom\n\nFrom override", callback.getToolDefinition().description());
    }

    @SmartaxMcpTool
    static class DummyMcpTool {
        @Tool(name = "ping", description = "ping")
        public String ping() {
            return "pong";
        }
    }

    @SmartaxMcpTool
    static class OverrideMcpTool {
        @Tool(name = "ping", description = "ping")
        @ToolDescriptionFile("custom-doc")
        public String ping() {
            return "pong";
        }
    }
}
