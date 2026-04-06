package ru.axenix.smartax.dui.service.application.page.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий взаимодействия с БД таблицы <strong>page</strong>
 *
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

    /**
     * Получение всех страниц, отсортированных по имени.
     *
     * @return список страниц.
     */
    List<PageEntity> findAllByOrderByNameAsc();
}
