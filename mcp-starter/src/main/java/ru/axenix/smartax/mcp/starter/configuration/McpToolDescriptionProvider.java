package ru.axenix.smartax.mcp.starter.configuration;

import org.springframework.ai.tool.annotation.Tool;

import java.lang.reflect.Method;
import java.util.Optional;

public interface McpToolDescriptionProvider {

    Optional<String> resolveDescription(Tool tool, Method method);
}
