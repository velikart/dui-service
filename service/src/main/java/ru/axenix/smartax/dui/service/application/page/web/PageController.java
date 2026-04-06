package ru.axenix.smartax.dui.service.application.page.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.api.PagesApi;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

@RestController
@RequiredArgsConstructor
public class PageController implements PagesApi {

    private final PageService pageService;

    @Override
    @Authorization
    public PageDto getPageByName(String name) {
        return pageService.getPageByName(name);
    }

    @Override
    @Authorization
    public Object getPageInstructions(String name) {
        return pageService.getPageByName(name).getInstructions();
    }
}
