package ru.axenix.smartax.dui.service.application.template.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.axenix.smartax.dui.service.application.template.model.TemplateType;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий взаимодействия с БД таблицы <strong>template</strong>
 *
 * @author Velikanov Artyom.
 */
@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {

    /**
     * Запрос получения всех шаблонов определенного типа
     *
     * @param type Тип шаблона
     * @return Список шаблонов
     */
    List<TemplateEntity> findAllByType(TemplateType type);
}
