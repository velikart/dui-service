package ru.axenix.smartax.dui.service.application.page.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.mapper.PageMapper;
import ru.axenix.smartax.dui.service.application.page.service.impl.PageServiceImpl;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.error.ApplicationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class PageServiceImplTest {

    @Mock
    private PageRepository pageRepository;

    private PageServiceImpl pageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PageMapper pageMapper = new PageMapper(new ObjectMapper());
        pageService = new PageServiceImpl(pageRepository, pageMapper);
    }

    @Test
    void testGetPage_ExistingPage() {
        UUID pageId = UUID.randomUUID();

        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(pageId);
        pageEntity.setTitle("Test Title");
        pageEntity.setInstructions("{\"key\":\"value\"}");
        pageEntity.setAuthor("Test Author");
        pageEntity.setUpdateDateTime(LocalDateTime.now());

        when(pageRepository.findById(pageId)).thenReturn(Optional.of(pageEntity));

        PageDto pageDto = pageService.getPage(pageId);

        assertNotNull(pageDto);
        assertEquals(pageId, pageDto.getId());
        assertEquals("Test Title", pageDto.getTitle());
        assertNotNull(pageDto.getInstructions());
        assertEquals("value", ((Map<?, ?>) pageDto.getInstructions()).get("key"));
        assertEquals("Test Author", pageDto.getAuthor());
        assertNotNull(pageDto.getUpdateDateTime());
    }

    @Test
    void testGetPage_NonExistingPage() {
        UUID pageId = UUID.randomUUID();

        when(pageRepository.findById(pageId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> pageService.getPage(pageId));
    }

    @Test
    void testGetPageByName_ExistingPage() {
        final String pageName = "TestPage";

        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(UUID.randomUUID());
        pageEntity.setTitle("Test Title");
        pageEntity.setInstructions("{\"key\":\"value\"}");
        pageEntity.setAuthor("Test Author");
        pageEntity.setUpdateDateTime(LocalDateTime.now());

        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.of(pageEntity));

        PageDto pageDto = pageService.getPageByName(pageName);

        assertNotNull(pageDto);
        assertEquals("Test Title", pageDto.getTitle());
        assertNotNull(pageDto.getInstructions());
        assertEquals("value", ((Map<?, ?>) pageDto.getInstructions()).get("key"));
        assertEquals("Test Author", pageDto.getAuthor());
        assertNotNull(pageDto.getUpdateDateTime());
    }

    @Test
    void testGetPageByName_NonExistingPage() {
        String pageName = "NonExistentPage";

        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> pageService.getPageByName(pageName));
    }
}