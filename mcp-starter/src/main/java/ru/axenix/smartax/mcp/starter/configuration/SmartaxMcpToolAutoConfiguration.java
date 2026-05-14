package ru.axenix.smartax.mcp.starter.configuration;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.axenix.smartax.mcp.starter.annotation.SmartaxMcpTool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(SmartaxMcpToolProperties.class)
public class SmartaxMcpToolAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(McpToolDescriptionMarkdownProvider.class)
    public McpToolDescriptionMarkdownProvider mcpToolDescriptionMarkdownProvider(SmartaxMcpToolProperties properties) {
        return new McpToolDescriptionMarkdownProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "mcpTools")
    @ConditionalOnBean(McpToolDescriptionMarkdownProvider.class)
    public ToolCallbackProvider mcpTools(
            ApplicationContext applicationContext,
            List<McpToolDescriptionProvider> descriptionProviders
    ) {
        Collection<Object> toolServices = applicationContext.getBeansWithAnnotation(SmartaxMcpTool.class).values();
        List<ToolCallback> callbacks = new ArrayList<>();

        for (Object toolService : toolServices) {
            for (Method method : toolService.getClass().getMethods()) {
                Tool tool = method.getAnnotation(Tool.class);
                if (tool == null) {
                    continue;
                }

                ToolDefinition toolDefinition = ToolDefinitions.builder(method)
                        .description(resolveToolDescription(descriptionProviders, tool, method))
                        .build();

                callbacks.add(MethodToolCallback.builder()
                        .toolObject(toolService)
                        .toolMethod(method)
                        .toolDefinition(toolDefinition)
                        .build());
            }
        }

        return ToolCallbackProvider.from(callbacks);
    }

    private String resolveToolDescription(List<McpToolDescriptionProvider> descriptionProviders, Tool tool, Method method) {
        for (McpToolDescriptionProvider descriptionProvider : descriptionProviders) {
            var resolvedDescription = descriptionProvider.resolveDescription(tool, method);
            if (resolvedDescription.isPresent()) {
                return resolvedDescription.get();
            }
        }

        return tool.description();
    }
}
