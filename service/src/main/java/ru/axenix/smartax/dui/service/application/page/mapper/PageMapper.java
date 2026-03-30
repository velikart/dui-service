package ru.axenix.smartax.dui.service.application.page.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PageMapper {

    private final ObjectMapper objectMapper;

    public PageDto toDto(PageEntity entity) {
        if (entity == null) {
            return null;
        }

        PageDto dto = new PageDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setUpdateDateTime(entity.getUpdateDateTime());
        dto.setInstructions(parseInstructions(entity.getInstructions()));

        return dto;
    }

    private Map<String, Object> parseInstructions(String instructions) {
        if (instructions == null) {
            return java.util.Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(
                    instructions,
                    new TypeReference<Map<String, Object>>() { }
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse instructions JSON", e);
        }
    }
}