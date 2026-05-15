package ru.axenix.smartax.dui.service.application.page.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Модель для хранения страниц UI
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "page")
public class PageEntity {
    /**
     * Идентификатор страницы
     */
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id")
    private UUID id;

    /**
     * Название страницы по которому она доступна
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * Наименование страницы
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
     * Создатель записи
     */
    private String author;

    /**
     * Дата и время обновления
     */
    @UpdateTimestamp
    private LocalDateTime updateDateTime;
}
