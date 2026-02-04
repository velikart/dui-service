package ru.axenix.smartax.dui.service.integration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.axenix.smartax.dui.service.model.table.TableResponse;
import ru.axenix.smartax.printservice.model.templateview.find.FindTemplateResponseDto;
import ru.axenix.smartax.printservice.model.templateview.find.TemplateMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper для преобразования ответа поиска шаблонов {@link FindTemplateResponseDto}
 * в универсальный ответ для sdi-table {@link TableResponse}.
 *
 * @author Artem Velikanov.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TemplateTableMapper {

    /**
     * Преобразование ответа поиска шаблонов в TableResponse.
     *
     * @param source DTO ответа поиска шаблонов.
     * @return универсальный ответ для sdi-table.
     */
    @Mapping(
            target = "total",
            expression = "java(source.getPage() != null && source.getPage().getTotalNumberOfRows() != null "
                    + "? source.getPage().getTotalNumberOfRows().intValue() : 0)"
    )
    @Mapping(
            target = "records",
            expression = "java(templatesToRecords(source.getTemplates()))"
    )
    TableResponse toTableResponse(FindTemplateResponseDto source);

    /**
     * Преобразование списка TemplateMeta в список записей таблицы.
     *
     * @param templates список метаинформации о шаблонах.
     * @return список записей в формате Map&lt;String, Object&gt;.
     */
    default List<Map<String, Object>> templatesToRecords(List<TemplateMeta> templates) {
        if (templates == null) {
            return new ArrayList<>();
        }

        return templates.stream()
                .map(this::templateToRecord)
                .toList();
    }

    /**
     * Преобразование одного TemplateMeta в запись таблицы.
     *
     * @param t метаинформация о шаблоне.
     * @return запись таблицы.
     */
    default Map<String, Object> templateToRecord(TemplateMeta t) {
        Map<String, Object> map = new HashMap<>();
        if (t == null) {
            return map;
        }

        map.put("templateId", t.getTemplateId());
        map.put("templateCode", t.getTemplateCode());
        map.put("templateGroup", t.getTemplateGroup());
        map.put("description", t.getDescription());
        map.put("schema", t.getSchema());
        map.put("creationDate", t.getCreationDate());
        map.put("updateDate", t.getUpdateDate());

        return map;
    }
}

