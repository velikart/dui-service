package ru.axenix.smartax.dui.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;

class ApplicationTest {
    @Test
    void testMain() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(Application.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            Application.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(Application.class, new String[]{}));
        }
    }
}