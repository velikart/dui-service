package ru.axenix.smartax.dui.service.model.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Универсальная модель для поля file.
 *
 * @author Artem Velikanov.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {

    /**
     * Идентификатор файла.
     */
    @Schema(description = "Идентификатор файла")
    private String uuid;

    /**
     * Наименование файла.
     */
    @Schema(description = "Наименование файла")
    private String name;

    /**
     * Контент файла в формате base64.
     */
    @Schema(description = "Контент файла в формате base64")
    private String content;

}
