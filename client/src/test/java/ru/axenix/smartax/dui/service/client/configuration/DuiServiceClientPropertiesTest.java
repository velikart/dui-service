package ru.axenix.smartax.dui.service.client.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuiServiceClientPropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        DuiServiceClientProperties properties = new DuiServiceClientProperties();

        assertEquals("http://localhost:8080", properties.getBaseUrl());
        assertEquals(2000, properties.getConnectTimeoutMs());
        assertEquals(5000, properties.getReadTimeoutMs());
    }

    @Test
    void shouldSetAndGetValues() {
        DuiServiceClientProperties properties = new DuiServiceClientProperties();

        properties.setBaseUrl("http://dui-service:8081");
        properties.setConnectTimeoutMs(3000);
        properties.setReadTimeoutMs(7000);

        assertEquals("http://dui-service:8081", properties.getBaseUrl());
        assertEquals(3000, properties.getConnectTimeoutMs());
        assertEquals(7000, properties.getReadTimeoutMs());
    }
}