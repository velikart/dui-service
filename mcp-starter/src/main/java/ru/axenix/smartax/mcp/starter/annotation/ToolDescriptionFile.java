package ru.axenix.smartax.mcp.starter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolDescriptionFile {

    /**
     * Markdown filename for tool description.
     * Supports values with or without .md extension.
     */
    String value();
}
