package ru.axenix.smartax.dui.service.application.page.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.mapper.PageMapper;
import ru.axenix.smartax.dui.service.application.page.model.PageShortDto;
import ru.axenix.smartax.dui.service.application.page.service.impl.PageServiceImpl;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.error.ApplicationException;

import java.util.List;
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

    @Test
    void testListPages() {
        UUID pageId = UUID.randomUUID();
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(pageId);
        pageEntity.setName("dashboard");
        pageEntity.setTitle("Dashboard");
        when(pageRepository.findAllByOrderByNameAsc()).thenReturn(List.of(pageEntity));

        List<PageShortDto> pages = pageService.listPages();

        assertNotNull(pages);
        assertEquals(1, pages.size());
        assertEquals(pageId, pages.get(0).id());
        assertEquals("dashboard", pages.get(0).name());
        assertEquals("Dashboard", pages.get(0).title());
        verify(pageRepository).findAllByOrderByNameAsc();
    }

    @Test
    void testListPages_Empty() {
        when(pageRepository.findAllByOrderByNameAsc()).thenReturn(List.of());

        List<PageShortDto> pages = pageService.listPages();

        assertNotNull(pages);
        assertTrue(pages.isEmpty());
        verify(pageRepository).findAllByOrderByNameAsc();
    }
}
