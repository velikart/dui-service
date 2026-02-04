package ru.axenix.smartax.dui.service.application.page.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель для хранения страниц UI
 *
 * @author Sergey Dresvyanin.
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
     * Наименование старницы
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Объект json страницы
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private String instructions;

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
