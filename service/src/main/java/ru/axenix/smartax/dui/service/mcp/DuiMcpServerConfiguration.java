package ru.axenix.smartax.dui.service.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.axenix.smartax.dui.service.mcp.tool.DuiMcpTool;

import java.util.Collection;

/**
 * Конфигурация MCP-сервера DUI.
 * <p>
 * Регистрирует в Spring AI callback-провайдер, который автоматически
 * подхватывает все бины, помеченные аннотацией {@link DuiMcpTool}.
 */
@Configuration
public class DuiMcpServerConfiguration {

    /**
     * Формирует провайдер MCP-инструментов на основе всех {@link DuiMcpTool} в контексте.
     *
     * @param applicationContext spring-контекст приложения
     * @return провайдер callback-методов MCP-инструментов
     */
    @Bean
    public ToolCallbackProvider mcpTools(ApplicationContext applicationContext) {
        Collection<Object> toolServices = applicationContext.getBeansWithAnnotation(DuiMcpTool.class).values();
        return MethodToolCallbackProvider.builder()
                .toolObjects(toolServices.toArray())
                .build();
    }
}

