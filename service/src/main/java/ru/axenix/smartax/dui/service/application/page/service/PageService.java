package ru.axenix.smartax.dui.service.application.page.service;

import ru.axenix.smartax.dui.service.application.page.model.PageDto;

import java.util.UUID;

/**
 * Сервис для работы со страницами
 *
 * @author Sergey Dresvyanin
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
}