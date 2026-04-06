package ru.axenix.smartax.dui.service.client.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Внешние свойства конфигурации клиента DUI Service.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "dui-service.client")
public class DuiServiceClientProperties {

    /**
     * Базовый URL DUI Service.
     */
    private String baseUrl = "http://localhost:8080";

    /**
     * Таймаут установления соединения в миллисекундах.
     */
    private int connectTimeoutMs = 2000;

    /**
     * Таймаут чтения ответа в миллисекундах.
     */
    private int readTimeoutMs = 5000;

}
