package ru.axenix.smartax.dui.service.application.template.service;

import org.springframework.core.io.Resource;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateFilterDto;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса управления шаблонами.
 *
 * @author Velikanov Artyom.
 */
public interface TemplateService {

    /**
     * Получение списка шаблонов по фильтру.
     *
     * @param filter параметры фильтрации шаблонов.
     * @return Список информации по шаблонам.
     */
    List<TemplateDto> getTemplates(TemplateFilterDto filter);

    /**
     * Получение файла json-инструкции шаблона по идентификатору.
     *
     * @param uuid идентификатор шаблона.
     * @return Файл json-инструкции.
     */
    Resource getTemplatePage(UUID uuid);

    /**
     * Получение изображения шаблона по идентификатору.
     *
     * @param uuid идентификатор шаблона.
     * @return Изображение шаблона.
     */
    FileDto getTemplateImage(UUID uuid);
}
