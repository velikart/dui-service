package ru.axenix.smartax.dui.service.application.collection.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Доменная модель <strong>Запись коллекции</strong>.
 *
 * @author Velikanov Artyom.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "collection")
public class CollectionEntity {

    /**
     * Технический идентификатор записи коллекции.
     */
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "history_uuid")
    private UUID historyId;

    /**
     * Технический идентификатор коллекции (к которой относится данная запись).
     */
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "collection_uuid")
    private UUID collectionId;

    /**
     * Дата создания записи в коллекции.
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    /**
     * Признак, что запись коллекции является текущей используемой.
     */
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    @Column(name = "is_current", columnDefinition = "BIT")
    private Boolean isCurrent;

    /**
     * Идентификатор пользователя создавшего коллекцию.
     */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Заголовок (название) коллекции.
     */
    @Column(name = "title", nullable = false)
    private String title;


    /**
     * Список инструкций страниц коллекции.
     */
    @NotNull
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<Map<String, Object>> pages = new ArrayList<>();

    /**
     * Список моков запросов коллекции.
     */
    @NotNull
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<Map<String, Object>> mocks = new ArrayList<>();

    /**
     * Конфигурация стилей коллекции.
     */
    @NotNull
    @Builder.Default
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> config = new HashMap<>();
}
