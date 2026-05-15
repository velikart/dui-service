package ru.axenix.smartax.dui.service.application.page.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PageMapperTest {

    private PageMapper pageMapper;

    @BeforeEach
    void setUp() {
        pageMapper = new PageMapper();
    }

    @Test
    void toDtoSuccess() {
        UUID id = UUID.randomUUID();

        PageEntity entity = new PageEntity();
        entity.setId(id);
        entity.setName("dashboard");
        entity.setTitle("Title");
        entity.setPages(List.of(Map.of("page", "one")));
        entity.setMocks(List.of(Map.of("mock", "ok")));
        entity.setAuthor("Author");
        entity.setUpdateDateTime(LocalDateTime.now());

        PageDto dto = pageMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("dashboard", dto.getName());
        assertEquals("Title", dto.getTitle());
        assertEquals("Author", dto.getAuthor());
        assertNotNull(dto.getUpdateDateTime());
        assertNotNull(dto.getPages());
        assertNotNull(dto.getMocks());
        assertEquals("one", dto.getPages().get(0).get("page"));
        assertEquals("ok", dto.getMocks().get(0).get("mock"));
    }

    @Test
    void toDtoNullPagesAndMocks() {
        PageEntity entity = new PageEntity();
        entity.setPages(null);
        entity.setMocks(null);

        PageDto dto = pageMapper.toDto(entity);

        assertNotNull(dto);
        assertNotNull(dto.getPages());
        assertNotNull(dto.getMocks());
    }

    @Test
    void toDtoNullEntity() {
        PageDto dto = pageMapper.toDto(null);

        assertEquals(null, dto);
    }

    @Test
    void toEntitySuccess() {
        UUID id = UUID.randomUUID();
        PageDto dto = new PageDto();
        dto.setName("dashboard");
        dto.setTitle("Title");
        dto.setPages(List.of(Map.of("page", "one")));
        dto.setMocks(List.of(Map.of("mock", "ok")));
        dto.setAuthor("Author");

        PageEntity entity = pageMapper.toEntity(dto, id);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("dashboard", entity.getName());
        assertEquals("Title", entity.getTitle());
        assertEquals("one", entity.getPages().get(0).get("page"));
        assertEquals("ok", entity.getMocks().get(0).get("mock"));
        assertEquals("Author", entity.getAuthor());
    }

    @Test
    void mergeEntitySuccess() {
        PageEntity entity = new PageEntity();
        entity.setName("old");
        entity.setTitle("Old title");
        entity.setPages(List.of(Map.of("page", "old")));
        entity.setMocks(List.of(Map.of("mock", "old")));
        entity.setAuthor("old-author");

        PageDto dto = new PageDto();
        dto.setName("new");
        dto.setTitle("New title");
        dto.setPages(List.of(Map.of("page", "new")));
        dto.setMocks(List.of(Map.of("mock", "new")));
        dto.setAuthor("new-author");

        pageMapper.merge(entity, dto);

        assertEquals("new", entity.getName());
        assertEquals("New title", entity.getTitle());
        assertEquals("new", entity.getPages().get(0).get("page"));
        assertEquals("new", entity.getMocks().get(0).get("mock"));
        assertEquals("new-author", entity.getAuthor());
    }
}