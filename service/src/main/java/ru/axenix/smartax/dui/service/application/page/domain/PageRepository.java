package ru.axenix.smartax.dui.service.application.page.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий взаимодействия с БД таблицы <strong>page</strong>
 *
 * @author Sergey Dresvyanin.
 */
@Repository
public interface PageRepository extends JpaRepository<PageEntity, UUID> {

    /**
     * Получение страницы по уникальному имени
     *
     * @param name название он же адрес страницы
     * @return Объект страницы
     */
    Optional<PageEntity> findByNameEqualsIgnoreCase(String name);
}
