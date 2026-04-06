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

import ru.axenix.smartax.dui.service.client.invoker.ApiClient;

/**
 * Автоконфигурация сгенерированного клиента DUI Service.
 */
@AutoConfiguration
@ConditionalOnClass(ApiClient.class)
@EnableConfigurationProperties(DuiServiceClientProperties.class)
@Import(DuiServiceApiRegistrar.class)
public class DuiServiceClientAutoConfiguration {

    /**
     * Создает и настраивает {@link ApiClient} для вызовов DUI Service.
     *
     * @param props свойства клиента DUI Service
     * @param webClientBuilder билдер Spring {@link WebClient}
     * @return настроенный экземпляр {@link ApiClient}
     */
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
