package ru.axenix.smartax.dui.service.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "Количество записей по статусам dispatching/table")
public class DispatchingStatusCountsResponse {

    @Schema(description = "Количество по каждому статусу в порядке следования статусов")
    private List<String> items;

    @Schema(description = "Общее количество записей")
    private String total;
}
