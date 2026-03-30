package ru.axenix.smartax.dui.service.client.api;

import ru.axenix.smartax.dui.service.client.invoker.ApiClient;

public class TestApi {

    private final ApiClient apiClient;

    public TestApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}