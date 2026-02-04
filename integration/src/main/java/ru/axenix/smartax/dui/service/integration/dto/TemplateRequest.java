package ru.axenix.smartax.dui.service.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.axenix.smartax.dui.service.model.table.FileResponseDto;

/**
 * Модель создания шаблона печатной формы для print-service.
 *
 * @author Artem Velikanov.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

    /**
     * Код шаблона.
     */
    @Schema(description = "Код шаблона")
    private String templateCode;

    /**
     * Описание шаблона.
     */
    @Schema(description = "Описание шаблона")
    private String description;

    /**
     * Модель файла.
     */
    @Schema(description = "Модель файла")
    private FileResponseDto file;

}
