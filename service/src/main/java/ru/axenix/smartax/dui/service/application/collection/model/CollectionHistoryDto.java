package ru.axenix.smartax.dui.service.application.collection.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Модель истории коллекции.
 *
 * @author Velikanov Artyom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionHistoryDto {

    /**
     * Идентификатор записи коллекции
     */
    @Schema(description = "Идентификаторы записи коллекции")
    private UUID uuid;

    /**
     * Наименование коллекции
     */
    @Schema(description = "Наименование коллекции")
    private String title;

    /**
     * Дата создания записи коллекции
     */
    @Schema(description = "Дата создания записи коллекции")
    private LocalDateTime creationDate;

    /**
     * Признак текущей актуальной используемой коллекции
     */
    @Schema(description = "Признак текущей актуальной используемой коллекции")
    private Boolean isCurrent;

    /**
     * Объект json со списком страниц
     */
    @NotNull
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Schema(description = "Объект json со списком страниц")
    private List<Map<String, Object>> pages = new ArrayList<>();

    /**
     * Объект json со списком моков запросов
     */
    @NotNull
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Schema(description = "Объект json со списком моков запросов")
    private List<Map<String, Object>> mocks = new ArrayList<>();

    /**
     * Объект json конфигурации стилей коллекции.
     */
    @NotNull
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Schema(description = "Объект json конфигурации стилей коллекции")
    private Map<String, Object> config = new HashMap<>();
}