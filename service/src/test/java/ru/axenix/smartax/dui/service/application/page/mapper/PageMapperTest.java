package ru.axenix.smartax.dui.service.application.page.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageMapperTest {

    private PageMapper pageMapper;

    @BeforeEach
    void setUp() {
        pageMapper = new PageMapper(new ObjectMapper());
    }

    @Test
    void toDtoSuccess() {
        UUID id = UUID.randomUUID();
        String json = "{\"key\":\"value\"}";

        PageEntity entity = new PageEntity();
        entity.setId(id);
        entity.setTitle("Title");
        entity.setInstructions(json);
        entity.setAuthor("Author");
        entity.setUpdateDateTime(LocalDateTime.now());

        PageDto dto = pageMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Author", dto.getAuthor());
        assertNotNull(dto.getUpdateDateTime());
        assertNotNull(dto.getInstructions());
        assertEquals("value", ((Map<?, ?>) dto.getInstructions()).get("key"));
    }

    @Test
    void toDtoNullInstructions() {
        PageEntity entity = new PageEntity();
        entity.setInstructions(null);

        PageDto dto = pageMapper.toDto(entity);

        assertNotNull(dto);
        assertNotNull(dto.getInstructions());
        assertTrue(((Map<?, ?>) dto.getInstructions()).isEmpty());
    }

    @Test
    void toDtoInvalidJson() {
        PageEntity entity = new PageEntity();
        entity.setInstructions("invalid-json");

        assertThrows(IllegalStateException.class, () -> pageMapper.toDto(entity));
    }

    @Test
    void toDtoNullEntity() {
        PageDto dto = pageMapper.toDto(null);

        assertEquals(null, dto);
    }
}