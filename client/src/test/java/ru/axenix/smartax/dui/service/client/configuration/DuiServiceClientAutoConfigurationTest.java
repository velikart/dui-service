package ru.axenix.smartax.dui.service.client.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import ru.axenix.smartax.dui.service.client.invoker.ApiClient;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DuiServiceClientAutoConfigurationTest {

    @Test
    void duiApiClientShouldCreateConfiguredApiClient() throws Exception {
        DuiServiceClientProperties properties = new DuiServiceClientProperties();
        properties.setBaseUrl("http://localhost:18080");
        properties.setConnectTimeoutMs(1234);
        properties.setReadTimeoutMs(5678);

        DuiServiceClientAutoConfiguration configuration = new DuiServiceClientAutoConfiguration();

        ApiClient apiClient = configuration.duiApiClient(properties, WebClient.builder());

        assertNotNull(apiClient);
        assertEquals("http://localhost:18080", extractBasePath(apiClient));
    }

    private String extractBasePath(ApiClient apiClient) throws Exception {
        try {
            return (String) apiClient.getClass().getMethod("getBasePath").invoke(apiClient);
        } catch (NoSuchMethodException ex) {
            Field field = apiClient.getClass().getDeclaredField("basePath");
            field.setAccessible(true);
            return (String) field.get(apiClient);
        }
    }
}