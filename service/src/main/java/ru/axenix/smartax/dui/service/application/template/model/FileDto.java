package ru.axenix.smartax.dui.service.application.template.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.core.io.Resource;

/**
 * Модель файла.
 *
 * @author Velikanov Artyom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель файла")
public class FileDto {

    /**
     * Название файла
     */
    @Schema(description = "Название файла")
    private String filename;

    /**
     * Контент файла
     */
    @Schema(description = "Контент файла")
    private Resource file;
}