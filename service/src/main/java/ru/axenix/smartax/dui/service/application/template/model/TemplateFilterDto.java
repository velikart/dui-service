package ru.axenix.smartax.dui.service.application.template.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель фильтрации шаблона.
 *
 * @author Velikanov Artyom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель фильтрации шаблона")
public class TemplateFilterDto {

    /**
     * Тип шаблона (Страница/Компонент)
     */
    @Schema(description = "Тип шаблона (Страница/Компонент)")
    private TemplateType type;
}