package ru.axenix.smartax.dui.service.application.template.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.application.template.service.TemplateService;
import ru.axenix.smartax.dui.service.contract.api.TemplatesApi;
import ru.axenix.smartax.dui.service.contract.model.TemplateDto;
import ru.axenix.smartax.dui.service.contract.model.TemplateFilterDto;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления шаблонами страниц. Включает методы получения списка шаблонов, страниц и изображений.
 */
@RestController
@RequiredArgsConstructor
public class TemplateController implements TemplatesApi {

    private final TemplateService templateService;

    @Override
    @Authorization
    public List<TemplateDto> getTemplates(TemplateFilterDto filter) {
        return templateService.getTemplates(filter);
    }

    @Override
    @Authorization
    public Resource getTemplatePage(UUID uuid) {
        return templateService.getTemplatePage(uuid);
    }

    @Override
    @Authorization
    public Resource getTemplateImage(UUID uuid) {
        return templateService.getTemplateImage(uuid).getFile();
    }
}