package ru.axenix.smartax.dui.service.application.page.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.service.impl.PageServiceImpl;
import ru.axenix.smartax.dui.service.error.ApplicationException;
import ru.axenix.smartax.dui.service.application.page.model.PageDto;

class PageServiceImplTest {

    @Mock
    private PageRepository pageRepository;

    @InjectMocks
    private PageServiceImpl pageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPage_ExistingPage() {
        // Arrange
        UUID pageId = UUID.randomUUID();
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(pageId);
        pageEntity.setTitle("Test Title");
        pageEntity.setInstructions("Test Instructions");
        pageEntity.setAuthor("Test Author");
        pageEntity.setUpdateDateTime(LocalDateTime.now());
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(pageEntity));
        PageDto pageDto = pageService.getPage(pageId);
        assertNotNull(pageDto);
        assertEquals(pageId, pageDto.getId());
        assertEquals("Test Title", pageDto.getTitle());
        assertEquals("Test Instructions", pageDto.getInstructions());
        assertEquals("Test Author", pageDto.getAuthor());
        assertNotNull(pageDto.getUpdateDateTime());
    }

    @Test
    void testGetPage_NonExistingPage() {
        UUID pageId = UUID.randomUUID();
        when(pageRepository.findById(pageId)).thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> {
            pageService.getPage(pageId);
        });
    }

    @Test
    void testGetPageByName_ExistingPage() {
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(UUID.randomUUID());
        pageEntity.setTitle("Test Title");
        pageEntity.setInstructions("Test Instructions");
        pageEntity.setAuthor("Test Author");
        pageEntity.setUpdateDateTime(LocalDateTime.now());
        String pageName = "TestPage";
        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.of(pageEntity));
        PageDto pageDto = pageService.getPageByName(pageName);
        assertNotNull(pageDto);
        assertEquals("Test Title", pageDto.getTitle());
        assertEquals("Test Instructions", pageDto.getInstructions());
        assertEquals("Test Author", pageDto.getAuthor());
        assertNotNull(pageDto.getUpdateDateTime());
    }

    @Test
    void testGetPageByName_NonExistingPage() {
        String pageName = "NonExistentPage";
        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> {
            pageService.getPageByName(pageName);
        });
    }
}
