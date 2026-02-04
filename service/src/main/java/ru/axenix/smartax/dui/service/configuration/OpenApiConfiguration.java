package ru.axenix.smartax.dui.service.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Open Api.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("DUI-SERVICE")
                        .description("Сервис администрирования Dynamic UI")
                        .version("v1.0")
                );
    }
}
