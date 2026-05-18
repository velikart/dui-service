package ru.axenix.smartax.dui.service.mcp.tool;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркерная аннотация для бинов, предоставляющих MCP-инструменты DUI.
 * <p>
 * Используется в {@code DuiMcpServerConfiguration} для автоматического поиска
 * и регистрации методов, аннотированных {@code @Tool}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DuiMcpTool {
}