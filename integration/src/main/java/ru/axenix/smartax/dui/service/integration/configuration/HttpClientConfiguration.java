package ru.axenix.smartax.dui.service.integration.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfiguration {

    @Bean(name = "seriesServiceRestTemplate")
    public RestTemplate seriesServiceRestTemplate() {
        return new RestTemplate();
    }
}
