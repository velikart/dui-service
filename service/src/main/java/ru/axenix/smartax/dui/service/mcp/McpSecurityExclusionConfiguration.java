package ru.axenix.smartax.dui.service.mcp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class McpSecurityExclusionConfiguration {

    @Bean
    public WebSecurityCustomizer mcpWebSecurityCustomizer() {
        return (WebSecurity web) -> web.ignoring().requestMatchers("/sse", "/mcp/**");
    }
}
