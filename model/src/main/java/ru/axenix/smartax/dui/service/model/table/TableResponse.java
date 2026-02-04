package ru.axenix.smartax.dui.service.model.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Универсальный ответ для sdi-table.
 *
 * @author Artem Velikanov.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Универсальный ответ для sdi-table")
public class TableResponse {

    /**
     * Общее количество записей
     */
    @Schema(description = "Общее количество записей")
    private int total;

    /**
     * Универсальный список объектов.
     * Каждый объект является словарём ключ → значение.
     */
    @Schema(description = "Универсальный список объектов. Каждый объект является словарём ключ → значение")
    private List<Map<String, Object>> records;
}