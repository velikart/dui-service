package ru.axenix.smartax.dui.service.application.page.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.api.PagesApi;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PageController implements PagesApi {

    private final PageService pageService;

    /**
     * Получение страницы по имени маршрута.
     *
     * @param pageName имя/маршрут страницы.
     * @return DTO страницы.
     */
    @Override
    @Authorization
    public PageDto getPageByName(String pageName) {
        return pageService.getPageByName(pageName);
    }

    /**
     * Получение страницы по идентификатору.
     *
     * @param pageUUID идентификатор страницы.
     * @return DTO страницы.
     */
    @Override
    @Authorization
    public PageDto getPage(UUID pageUUID) {
        return pageService.getPage(pageUUID);
    }

    /**
     * Получение списка страниц в упрощенном формате.
     *
     * @return список страниц.
     */
    @Override
    @Authorization
    public List<PageShortDto> getAllPages() {
        return pageService.listPages();
    }

    /**
     * Создание новой страницы.
     *
     * @param page DTO страницы.
     * @return созданная страница.
     */
    @Override
    @Authorization
    public PageDto createPage(PageDto page) {
        return pageService.createPage(page);
    }

    /**
     * Перезапись существующей страницы.
     *
     * @param pageUUID идентификатор страницы.
     * @param page     DTO страницы.
     * @return обновленная страница.
     */
    @Override
    @Authorization
    public PageDto editPage(UUID pageUUID, PageDto page) {
        return pageService.editPage(pageUUID, page);
    }
}
