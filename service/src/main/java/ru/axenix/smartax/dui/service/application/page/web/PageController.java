package ru.axenix.smartax.dui.service.application.page.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.application.page.model.PageDto;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

@RestController
@RequestMapping("/app/v1/page")
@RequiredArgsConstructor
@Tag(name = "Управление страницами", description = "Апи для получения страниц")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class PageController {

    private final PageService pageService;

    @Operation(
            tags = "Управление страницами",
            summary = "Получение объекта страницы",
            description = "Получение объекта страницы по маршруту",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @GetMapping
    public PageDto getPageByName(@RequestParam(name = "pageName") String name) {
        return pageService.getPageByName(name);
    }

    @Operation(
            tags = "Управление страницами",
            summary = "Получение json инструкции",
            description = "Получение json-инструкции страницы по маршруту страницы",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @GetMapping(value = "/instructions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getPageInstructions(@RequestParam(name = "pageName") String name) {
        return pageService.getPageByName(name).getInstructions();
    }
}
