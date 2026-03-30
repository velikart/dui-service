package ru.axenix.smartax.dui.service.client.configuration;

import java.time.Duration;

import io.netty.channel.ChannelOption;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

// ВАЖНО: пакет ДОЛЖЕН совпадать с invokerPackage из build.gradle
import ru.axenix.smartax.dui.service.client.invoker.ApiClient;

@AutoConfiguration
@ConditionalOnClass(ApiClient.class)
@EnableConfigurationProperties(DuiServiceClientProperties.class)
@Import(DuiServiceApiRegistrar.class)
public class DuiServiceClientAutoConfiguration {

    @Bean
    public ApiClient duiApiClient(DuiServiceClientProperties props, WebClient.Builder webClientBuilder) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(props.getReadTimeoutMs()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeoutMs());

        WebClient webClient = webClientBuilder
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(props.getBaseUrl());
        return apiClient;
    }
}
