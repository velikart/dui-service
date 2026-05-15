package ru.axenix.smartax.dui.service.application.page.service;


import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы со страницами
 */
public interface PageService {

    /**
     * Получение страницы по Id
     * @param pageId идентификатор страницы
     * @return объект страницы
     */
    PageDto getPage(UUID pageId);

    /**
     * Получение страницы по маршруту
     * @param name маршрут или имя страницы
     * @return бъект страницы
     */
    PageDto getPageByName(String name);

    /**
     * Получение списка страниц с краткой информацией.
     *
     * @return список страниц.
     */
    List<PageShortDto> listPages();

    /**
     * Создание новой страницы.
     *
     * @param page контент страницы.
     * @return созданная страница.
     */
    PageDto createPage(PageDto page);

    /**
     * Перезапись существующей страницы.
     *
     * @param pageId идентификатор страницы.
     * @param page   контент страницы.
     * @return обновленная страница.
     */
    PageDto editPage(UUID pageId, PageDto page);

}