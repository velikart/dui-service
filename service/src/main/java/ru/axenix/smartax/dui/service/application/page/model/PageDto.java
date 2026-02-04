package ru.axenix.smartax.dui.service.application.page.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель страницы.
 *
 * @author Sergey Dresvyanin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель страницы для микрофронта")
public class PageDto {
    /**
     * Идентификатор страницы
     */
    @Schema(description = "Идентификатор страницы")
    private UUID id;

    /**
     * Наименование старницы
     */
    @Schema(description = "Наименование старницы")
    private String title;

    /**
     * Название страницы по которому она доступна
     */
    @Schema(description = "Название страницы по которому она доступна")
    private String name;

    /**
     * Идентификатор страницы
     */
    @Schema(description = "Объект json страницы")
    private Object instructions;

    /**
     * Дата и время обновления
     */
    @Schema(description = "Дата и время обновления")
    private LocalDateTime updateDateTime;

    /**
     * Создатель записи
     */
    @Schema(description = "Создатель записи")
    private String author;
}