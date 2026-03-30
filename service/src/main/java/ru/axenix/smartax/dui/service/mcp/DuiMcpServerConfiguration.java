package ru.axenix.smartax.dui.service.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DuiMcpServerConfiguration {

    @Bean
    public ToolCallbackProvider mcpTools(List<DuiMcpToolService> toolServices) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(toolServices.toArray())
                .build();
    }
}

