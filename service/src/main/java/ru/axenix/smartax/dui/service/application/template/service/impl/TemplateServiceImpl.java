package ru.axenix.smartax.dui.service.application.template.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.axenix.smartax.dui.service.application.template.domain.TemplateRepository;
import ru.axenix.smartax.dui.service.application.template.service.TemplateService;
import ru.axenix.smartax.dui.service.error.ErrorDescription;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateFilterDto;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления шаблонами.
 * TODO Переделать на хранение шаблонов в бд и разработкать апи создания и управления шаблонами.
 *
 * @author Velikanov Artyom.
 */
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    /**
     * Получение списка шаблонов по фильтру.
     *
     * @param filter параметры фильтрации шаблонов.
     * @return Список информации по шаблонам.
     */
    @Override
    public List<TemplateDto> getTemplates(TemplateFilterDto filter) {
        var list = (filter == null || filter.getType() == null)
                ? templateRepository.findAll()
                : templateRepository.findAllByType(filter.getType());

        return list.stream()
                .map(it -> TemplateDto.builder()
                        .uuid(it.getUuid())
                        .title(it.getTitle())
                        .imageUrl("/app/v1/template/" + it.getUuid() + "/image")
                        .build())
                .toList();
    }

    /**
     * Получение файла json-инструкции шаблона по идентификатору.
     *
     * @param uuid идентификатор шаблона.
     * @return Файл json-инструкции.
     */
    @Override
    public Resource getTemplatePage(UUID uuid) {
        var template = templateRepository.findById(uuid)
                .orElseThrow(ErrorDescription.TEMPLATE_NOT_FOUND::exception);
        return getPage(template.getFilenamePage());
    }

    /**
     * Получение изображения шаблона по идентификатору.
     *
     * @param uuid идентификатор шаблона.
     * @return Изображение шаблона.
     */
    @Override
    public FileDto getTemplateImage(UUID uuid) {
        var template = templateRepository.findById(uuid)
                .orElseThrow(ErrorDescription.TEMPLATE_NOT_FOUND::exception);
        return new FileDto(template.getFilenameImage(), getImage(template.getFilenameImage()));
    }

    /**
     * Получение файла json-инструкции шаблона по наименованию файла.
     *
     * @param filenamePage наименование файла.
     * @return Файл json-инструкции.
     */
    private Resource getPage(String filenamePage) {
        Resource res = new ClassPathResource("template/page/" + filenamePage);
        if (!res.exists()) {
            throw ErrorDescription.TEMPLATE_PAGE_NOT_FOUND.exception();
        }
        return res;
    }

    /**
     * Получение файла изображения шаблона по наименованию файла.
     *
     * @param filenameImage наименование файла.
     * @return Файл изображения.
     */
    private Resource getImage(String filenameImage) {
        Resource res = new ClassPathResource("template/image/" + filenameImage);
        if (!res.exists()) {
            throw ErrorDescription.TEMPLATE_IMAGE_NOT_FOUND.exception();
        }
        return res;
    }
}
