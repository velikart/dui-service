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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.integration.dto.SeriesColumnsResponse;
import ru.axenix.smartax.dui.service.integration.service.SeriesColumnsService;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

@RestController
@RequestMapping("/app/v1/series")
@RequiredArgsConstructor
@Tag(name = "Интеграция DUI c series-service", description = "Получение колонок composition_table из внешнего API")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class SeriesColumnsController {

    private final SeriesColumnsService seriesColumnsService;

    @Operation(
            tags = "Интеграция с series-service",
            summary = "Получение основных колонок",
            description = "Возвращает первые 3 static_cols + все dynamic_cols",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/{seriesNum}/columns/main")
    public SeriesColumnsResponse getMainColumns(
            @PathVariable("seriesNum") String seriesNum,
            HttpServletRequest request
    ) {
        return seriesColumnsService.getMainColumns(seriesNum, request);
    }

    @Operation(
            tags = "Интеграция с series-service",
            summary = "Получение дополнительных колонок",
            description = "Возвращает все static_cols, начиная с 4-го",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/{seriesNum}/columns/additional")
    public SeriesColumnsResponse getAdditionalColumns(
            @PathVariable("seriesNum") String seriesNum,
            HttpServletRequest request
    ) {
        return seriesColumnsService.getAdditionalColumns(seriesNum, request);
    }
}
