package ru.axenix.smartax.dui.service.application.page.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PageMapper {


    public PageDto toDto(PageEntity entity) {
        if (entity == null) {
            return null;
        }

        PageDto dto = new PageDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setUpdateDateTime(entity.getUpdateDateTime());
        dto.setPages(entity.getPages() == null ? new ArrayList<>() : new ArrayList<>(entity.getPages()));
        dto.setMocks(entity.getMocks() == null ? new ArrayList<>() : new ArrayList<>(entity.getMocks()));

        return dto;
    }

    public PageEntity toEntity(PageDto dto, UUID pageId) {
        if (dto == null) {
            return null;
        }
        return PageEntity.builder()
            .id(pageId)
            .name(dto.getName())
            .title(dto.getTitle())
            .pages(dto.getPages() == null ? new ArrayList<>() : new ArrayList<>(dto.getPages()))
            .mocks(dto.getMocks() == null ? new ArrayList<>() : new ArrayList<>(dto.getMocks()))
            .author(dto.getAuthor())
            .build();
    }

    public void merge(PageEntity target, PageDto source) {
        if (target == null || source == null) {
            return;
        }

        target.setName(source.getName());
        target.setTitle(source.getTitle());
        target.setPages(source.getPages() == null ? new ArrayList<>() : new ArrayList<>(source.getPages()));
        target.setMocks(source.getMocks() == null ? new ArrayList<>() : new ArrayList<>(source.getMocks()));
        target.setAuthor(source.getAuthor());
    }
}