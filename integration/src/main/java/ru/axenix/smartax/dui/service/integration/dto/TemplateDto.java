package ru.axenix.smartax.dui.service.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.axenix.smartax.dui.service.model.table.FileResponseDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Модель получения шаблона печатной формы для print-service.
 *
 * @author Artem Velikanov.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {

    /**
     *  ID шаблона
     */
    @Schema(description = "ID шаблона")
    private UUID templateId;

    /**
     * Код шаблона
     */
    @Schema(description = "Код шаблона")
    private String templateCode;

    /**
     * Группа шаблона
     */
    @Schema(description = "Группа шаблона")
    private String templateGroup;

    /**
     * Описание шаблона
     */
    @Schema(description = "Описание шаблона")
    private String description;

    /**
     * JSON-schema плейсхолдеров
     */
    @Schema(description = "JSON-schema плейсхолдеров")
    private Map<String, Object> schema;

    /**
     * Дата создания
     */
    @Schema(description = "Дата создания")
    private LocalDateTime creationDate;

    /**
     * Дата обновления
     */
    @Schema(description = "Дата обновления")
    private LocalDateTime updateDate;

    /**
     * Файл шаблона
     */
    @Schema(description = "Файл шаблона")
    private FileResponseDto file;
}
