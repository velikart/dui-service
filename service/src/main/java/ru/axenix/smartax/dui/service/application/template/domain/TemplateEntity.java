package ru.axenix.smartax.dui.service.application.template.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.axenix.smartax.dui.service.application.template.model.TemplateType;

/**
 * Доменная модель <strong>Шаблон</strong>.
 *
 * @author Velikanov Artyom.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "template")
public class TemplateEntity {

    /**
     * Технический идентификатор шаблона.
     */
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    /**
     * Название шаблона.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Наименование файла шаблона инструкции страницы.
     */
    @Column(name = "filename_page", nullable = false)
    private String filenamePage;

    /**
     * Наименование названия файла изображения шаблона страницы.
     */
    @Column(name = "filename_image", nullable = false)
    private String filenameImage;

    /**
     * Тип шаблона (Страница/Отдельный компонент).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TemplateType type;
}
