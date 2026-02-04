package ru.axenix.smartax.dui.service.application.template.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.application.template.service.TemplateService;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateFilterDto;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления шаблонами страниц. Включает методы получения списка шаблонов, страниц и изображений.
 *
 * @author Velikanov Artyom.
 */
@RestController
@RequestMapping("/app/v1")
@RequiredArgsConstructor
@Tag(name = "Шаблоны страниц", description = "Управление шаблонами быстрой реализации страниц в IDE")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class TemplateController {

    private final TemplateService templateService;

    @Operation(
            tags = "Шаблоны страниц",
            summary = "Получение списка шаблонов страниц",
            description = "Получение списка шаблонов страниц без контента",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/template/list")
    public List<TemplateDto> getTemplates(@RequestBody(required = false) TemplateFilterDto filter) {
        return templateService.getTemplates(filter);
    }

    @Operation(
            tags = "Шаблоны страниц",
            summary = "Получение инструкции страницы шаблона",
            description = "Получение json-инструкции страницы по идентификатору шаблона",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/template/{uuid}/page")
    public Resource getTemplatePage(@PathVariable UUID uuid) {
        return templateService.getTemplatePage(uuid);
    }

    @Operation(
            tags = "Шаблоны страниц",
            summary = "Получение скрина (примера) страницы шаблона",
            description = "Получение скрина (примера) страницы по идентификатору шаблона",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/template/{uuid}/image")
    public ResponseEntity<Resource> getTemplateImage(@PathVariable UUID uuid) {
        FileDto image = templateService.getTemplateImage(uuid);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + image.getFilename());

        return ResponseEntity.ok()
                .headers(headers)
                .body(image.getFile());
    }
}
