package ru.axenix.smartax.dui.service.application.collection.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Модель списка коллекций.
 *
 * @author Velikanov Artyom.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель списка коллекций")
public class CollectionShortDto {

    /**
     * Идентификатор записи коллекции
     */
    @NotNull
    @Schema(description = "Идентификаторы записи коллекции")
    private UUID uuid;

    /**
     * Наименование коллекции
     */
    @NotNull
    @Schema(description = "Наименование коллекции")
    private String title;
}