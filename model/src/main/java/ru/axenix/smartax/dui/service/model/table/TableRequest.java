package ru.axenix.smartax.dui.service.model.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Универсальный запрос для sdi-table.
 *
 * @author Artem Velikanov.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Универсальный запрос для sdi-table")
public class TableRequest {

    /**
     * Поле для быстрого поиска
     */
    @Schema(description = "Поле для быстрого поиска")
    private String quickSearch;

    /**
     * Параметры фильтрации
     */
    @Schema(description = "Параметры фильтрации")
    private List<Object> filter;

    /**
     * Параметры сортировки
     */
    @Schema(description = "Параметры сортировки")
    private List<Object> sort;

    /**
     * Параметры пагинации
     */
    @Schema(description = "Параметры пагинации")
    private Pager pager;

    /**
     * Объект параметров пагинации.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pager {

        /**
         * Номер запрашиваемой страницы
         */
        @Schema(description = "Номер запрашиваемой страницы")
        private int page;

        /**
         * Количество запрашиваемых элементов страницы
         */
        @Schema(description = "Количество запрашиваемых элементов страницы")
        private int limit;

    }
}
