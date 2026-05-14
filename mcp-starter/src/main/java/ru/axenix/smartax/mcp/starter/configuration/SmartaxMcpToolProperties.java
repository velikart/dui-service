package ru.axenix.smartax.mcp.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smartax.mcp.tools")
public class SmartaxMcpToolProperties {

    /**
     * Relative or absolute path to markdown descriptions for MCP tools.
     * File name must match tool name and have .md extension.
     */
    private String descriptionPath = "docs/mcp-tools";

    public String getDescriptionPath() {
        return descriptionPath;
    }

    public void setDescriptionPath(String descriptionPath) {
        this.descriptionPath = descriptionPath;
    }
}
