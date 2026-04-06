package ru.axenix.smartax.dui.service.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class McpSecurityExclusionConfigurationTest {

    @Test
    void mcpWebSecurityCustomizer_NotNull() {
        McpSecurityExclusionConfiguration configuration = new McpSecurityExclusionConfiguration();

        WebSecurityCustomizer customizer = configuration.mcpWebSecurityCustomizer();

        assertNotNull(customizer);
    }
}
