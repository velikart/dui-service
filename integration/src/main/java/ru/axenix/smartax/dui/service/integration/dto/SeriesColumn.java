package ru.axenix.smartax.dui.service.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Колонка таблицы composition_table")
public class SeriesColumn {

    @Schema(description = "Техническое имя колонки")
    private String name;

    @Schema(description = "Заголовок колонки")
    private String title;

    @Schema(description = "Тип колонки")
    private String type;

    @Schema(description = "Признак вертикальной ориентации")
    private Boolean isVerticalOrientation;

    @Schema(description = "Ширина колонки")
    private String width;

    @Schema(description = "Редактируемость")
    private Boolean isReadonly;
}