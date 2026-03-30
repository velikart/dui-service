package ru.axenix.smartax.dui.service.mcp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

/**
 * HTTP Basic для транспорта MCP (/sse, /mcp/**). Логин/пароль — {@code dui.mcp.http-auth.*}
 * (обычно те же значения, что USERNAME/PASSWORD в {@code mcp.json} у клиента).
 */
@Configuration
@EnableWebSecurity
public class McpHttpSecurityConfiguration {

    @Bean
    @Order(1)
    public SecurityFilterChain mcpSecurityFilterChain(
            HttpSecurity http,
            @Value("${dui.mcp.http-auth.username:}") String username,
            @Value("${dui.mcp.http-auth.password:}") String password
    ) throws Exception {
        http.securityMatcher("/sse", "/mcp/**");
        http.csrf(csrf -> csrf.disable());
        if (StringUtils.hasText(username)) {
            var user = User.builder()
                    .username(username)
                    .password("{noop}" + password)
                    .roles("MCP")
                    .build();
            var uds = new InMemoryUserDetailsManager(user);
            var provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(uds);
            provider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
            http.authenticationManager(new ProviderManager(provider));
            http.authorizeHttpRequests(a -> a.anyRequest().authenticated());
            http.httpBasic(Customizer.withDefaults());
        } else {
            http.authorizeHttpRequests(a -> a.anyRequest().permitAll());
        }
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(a -> a.anyRequest().permitAll());
        return http.build();
    }
}
