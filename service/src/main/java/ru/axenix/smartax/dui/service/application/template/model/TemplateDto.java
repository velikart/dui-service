package ru.axenix.smartax.dui.service.application.template.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Модель шаблона.
 *
 * @author Velikanov Artyom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель шаблона")
public class TemplateDto {

    /**
     * Идентификатор шаблона
     */
    @Schema(description = "Идентификатор шаблона")
    private UUID uuid;

    /**
     * Наименование шаблона
     */
    @Schema(description = "Наименование шаблона")
    private String title;

    /**
     * Эндпоинт получения файла шаблона
     */
    @Schema(description = "Эндпоинт получения файла шаблона")
    private String imageUrl;
}