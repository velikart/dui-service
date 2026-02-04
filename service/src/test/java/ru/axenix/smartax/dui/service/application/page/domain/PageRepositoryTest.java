package ru.axenix.smartax.dui.service.application.page.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PageRepositoryTest {
    @Mock
    private PageRepository pageRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByNameEqualsIgnoreCase() {
        String pageName = "TestPage";
        UUID uuid = UUID.randomUUID();
        PageEntity expectedPage = new PageEntity(uuid, pageName, "title", "{}", "test", LocalDateTime.now());
        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.of(expectedPage));
        Optional<PageEntity> result = pageRepository.findByNameEqualsIgnoreCase(pageName);
        assertEquals(Optional.of(expectedPage), result);
    }

    @Test
    void testFindByNameEqualsIgnoreCase_NotFound() {
        String pageName = "NonExistentPage";
        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.empty());
        Optional<PageEntity> result = pageRepository.findByNameEqualsIgnoreCase(pageName);
        assertEquals(Optional.empty(), result);
    }
}
