package ru.axenix.smartax.dui.service.integration.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.axenix.smartax.dui.service.integration.dto.TemplateDto;
import ru.axenix.smartax.dui.service.model.table.FileResponseDto;
import ru.axenix.smartax.printservice.model.templateview.find.TemplateMeta;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    @Mapping(target = "file", source = "file")
    TemplateDto toDto(TemplateMeta meta, FileResponseDto file);
}
