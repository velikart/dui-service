package ru.axenix.smartax.dui.service.integration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.integration.dto.DispatchingStatusCountsResponse;
import ru.axenix.smartax.dui.service.integration.service.DispatchingStatusService;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

import java.util.Map;

@RestController
@RequestMapping("/app/v1/dispatching/table")
@RequiredArgsConstructor
@Tag(name = "Интеграция DUI c series-service", description = "Получение статистики статусов dispatching/table")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class DispatchingStatusController {

    private final DispatchingStatusService dispatchingStatusService;

    @Operation(
            tags = "Интеграция с series-service",
            summary = "Получение количества записей по статусам",
            description = "Вызывает /api/v1/dispatching/table и возвращает количество по каждому статусу и total",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/status-counts")
    public DispatchingStatusCountsResponse getStatusCounts(HttpServletRequest request) {
        return dispatchingStatusService.getStatusCounts(request);
    }

    @Operation(
            tags = "Интеграция с series-service",
            summary = "Проксирование dispatching table",
            description = "Вызывает /api/v1/dispatching/table с телом запроса и возвращает ответ без изменений",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping
    public Map<String, Object> getTableWithRecords(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request
    ) {
        return dispatchingStatusService.getTableWithRecords(payload, request);
    }
}
