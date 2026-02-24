package ru.axenix.smartax.dui.service.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "Ответ с колонками composition_table")
public class SeriesColumnsResponse {

    @Schema(description = "Массив колонок")
    private List<SeriesColumn> columns;
}
