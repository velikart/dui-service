package ru.axenix.smartax.dui.service.application.page.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import ru.axenix.smartax.common.security.SecurityContext;
import ru.axenix.smartax.common.security.UserInfo;
import ru.axenix.smartax.dui.service.application.page.domain.PageEntity;
import ru.axenix.smartax.dui.service.application.page.domain.PageRepository;
import ru.axenix.smartax.dui.service.application.page.filter.PermissionFilter;
import ru.axenix.smartax.dui.service.application.page.mapper.PageMapper;
import ru.axenix.smartax.dui.service.application.page.service.impl.PageServiceImpl;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;
import ru.axenix.smartax.dui.service.error.ApplicationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class PageServiceImplTest {

    @Mock
    private PageRepository pageRepository;

    private PageServiceImpl pageService;

    @Mock
    private PermissionFilter permissionFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PageMapper pageMapper = new PageMapper();
        pageService = new PageServiceImpl(pageRepository, pageMapper, permissionFilter);
    }

    @Test
    void testGetPage_ExistingPage() {
        UUID pageId = UUID.randomUUID();

        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(pageId);
        pageEntity.setTitle("Test Title");
        pageEntity.setPages(List.of(Map.of("key", "value")));
        pageEntity.setMocks(List.of(Map.of("mock", "m1")));
        pageEntity.setAuthor("Test Author");
        pageEntity.setUpdateDateTime(LocalDateTime.now());

        when(pageRepository.findById(pageId)).thenReturn(Optional.of(pageEntity));
        when(permissionFilter.filter(pageEntity.getPages())).thenReturn(pageEntity.getPages());
        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            PageDto pageDto = pageService.getPage(pageId);

            assertNotNull(pageDto);
            assertEquals(pageId, pageDto.getId());
            assertEquals("Test Title", pageDto.getTitle());
            assertNotNull(pageDto.getPages());
            assertEquals("value", pageDto.getPages().get(0).get("key"));
            assertEquals("Test Author", pageDto.getAuthor());
            assertNotNull(pageDto.getUpdateDateTime());
        }
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
        pageEntity.setPages(List.of(Map.of("key", "value")));
        pageEntity.setAuthor("Test Author");
        pageEntity.setUpdateDateTime(LocalDateTime.now());

        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.of(pageEntity));
        when(permissionFilter.filter(pageEntity.getPages())).thenReturn(pageEntity.getPages());
        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            PageDto pageDto = pageService.getPageByName(pageName);

            assertNotNull(pageDto);
            assertEquals("Test Title", pageDto.getTitle());
            assertNotNull(pageDto.getPages());
            assertEquals("value", pageDto.getPages().get(0).get("key"));
            assertEquals("Test Author", pageDto.getAuthor());
            assertNotNull(pageDto.getUpdateDateTime());
        }
    }

    @Test
    void testGetPageByName_FiltersByPermission() {
        final String pageName = "TestPage";
        PageEntity pageEntity = new PageEntity();
        pageEntity.setId(UUID.randomUUID());
        pageEntity.setTitle("Test Title");
        pageEntity.setPages(List.of(
            Map.of("key", "public"),
            Map.of("permission", List.of("PAGE_VIEW_SECURED"), "key", "secured")
        ));

        when(pageRepository.findByNameEqualsIgnoreCase(pageName)).thenReturn(Optional.of(pageEntity));
        List<Map<String, Object>> filteredPages = List.of(Map.of("key", "public"));
        when(permissionFilter.filter(pageEntity.getPages())).thenReturn(filteredPages);

        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            PageDto pageDto = pageService.getPageByName(pageName);

            assertEquals(1, pageDto.getPages().size());
            assertEquals("public", pageDto.getPages().get(0).get("key"));
        }
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
        assertEquals(new PageShortDto(pageId, "dashboard", "Dashboard"), pages.get(0));
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

    @Test
    void testCreatePage() {
        PageDto request = new PageDto();
        request.setName("new-page");
        request.setTitle("New page");
        request.setPages(List.of(Map.of("p", "1")));
        request.setMocks(List.of(Map.of("m", "1")));
        request.setAuthor("author");

        PageEntity savedEntity = new PageEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setName("new-page");
        savedEntity.setTitle("New page");
        savedEntity.setPages(List.of(Map.of("p", "1")));
        savedEntity.setMocks(List.of(Map.of("m", "1")));
        savedEntity.setAuthor("author");

        when(pageRepository.save(any(PageEntity.class))).thenReturn(savedEntity);

        PageDto result = pageService.createPage(request);

        assertNotNull(result.getId());
        assertEquals("new-page", result.getName());
        assertEquals("New page", result.getTitle());
        assertEquals("1", result.getPages().get(0).get("p"));
        assertEquals("1", result.getMocks().get(0).get("m"));
        verify(pageRepository).save(any(PageEntity.class));
    }

    @Test
    void testEditPage() {
        UUID pageId = UUID.randomUUID();

        PageEntity existingEntity = new PageEntity();
        existingEntity.setId(pageId);
        existingEntity.setName("old-page");
        existingEntity.setTitle("Old page");
        existingEntity.setPages(List.of(Map.of("p", "old")));
        existingEntity.setMocks(List.of(Map.of("m", "old")));
        existingEntity.setAuthor("author");

        PageDto request = new PageDto();
        request.setName("edited-page");
        request.setTitle("Edited page");
        request.setPages(List.of(Map.of("p", "new")));
        request.setMocks(List.of(Map.of("m", "new")));
        request.setAuthor("new-author");

        when(pageRepository.findById(pageId)).thenReturn(Optional.of(existingEntity));
        when(pageRepository.save(existingEntity)).thenReturn(existingEntity);

        PageDto result = pageService.editPage(pageId, request);

        assertEquals("edited-page", result.getName());
        assertEquals("Edited page", result.getTitle());
        assertEquals("new", result.getPages().get(0).get("p"));
        assertEquals("new", result.getMocks().get(0).get("m"));
        assertEquals("new-author", result.getAuthor());
        verify(pageRepository).findById(pageId);
        verify(pageRepository).save(existingEntity);
    }
}