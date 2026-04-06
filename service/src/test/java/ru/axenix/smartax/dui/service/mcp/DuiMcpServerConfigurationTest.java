package ru.axenix.smartax.dui.service.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.ApplicationContext;
import ru.axenix.smartax.dui.service.mcp.tool.DuiMcpTool;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DuiMcpServerConfigurationTest {

    @Test
    void mcpTools_CreatesProviderFromAnnotatedBeans() {
        DuiMcpServerConfiguration configuration = new DuiMcpServerConfiguration();
        ApplicationContext context = mock(ApplicationContext.class);
        when(context.getBeansWithAnnotation(DuiMcpTool.class)).thenReturn(Map.of("dummy", new DummyMcpTool()));

        ToolCallbackProvider provider = configuration.mcpTools(context);

        assertNotNull(provider);
    }

    @DuiMcpTool
    static class DummyMcpTool {
        @Tool(name = "ping", description = "ping")
        public String ping() {
            return "pong";
        }
    }
}
