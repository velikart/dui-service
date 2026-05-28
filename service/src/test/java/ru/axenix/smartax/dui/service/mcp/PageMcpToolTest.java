package ru.axenix.smartax.dui.service.mcp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageMcpToolTest {

    @Mock
    private PageService pageService;

    @Test
    void listPages_ReturnsPageShortDtoList() {
        PageMcpTool tools = new PageMcpTool(pageService);
        PageShortDto page = new PageShortDto(UUID.randomUUID(), "dashboard", "Dashboard");
        when(pageService.listPages()).thenReturn(List.of(page));

        List<PageShortDto> result = tools.listPages();

        assertEquals(1, result.size());
        assertEquals(page, result.get(0));
        verify(pageService).listPages();
    }

    @Test
    void getPageManifest_ReturnsManifestFromPageService() {
        PageMcpTool tools = new PageMcpTool(pageService);
        PageDto dto = new PageDto();
        dto.setPages(List.of(Map.of("key", "value")));
        when(pageService.getPageByName("dashboard")).thenReturn(dto);

        PageDto result = tools.getPageManifest("dashboard");

        assertEquals("value", result.getPages().get(0).get("key"));
        verify(pageService).getPageByName("dashboard");
    }

    @Test
    void savePageManifest_DelegatesToService() {
        PageMcpTool tools = new PageMcpTool(pageService);
        UUID pageId = UUID.randomUUID();
        PageDto dto = new PageDto();
        when(pageService.editPage(pageId, dto)).thenReturn(dto);

        PageDto result = tools.savePageManifest(pageId, dto);

        assertEquals(dto, result);
        verify(pageService).editPage(pageId, dto);
    }

    @Test
    void createPageManifest_DelegatesToService() {
        PageMcpTool tools = new PageMcpTool(pageService);
        PageDto dto = new PageDto();
        when(pageService.createPage(dto)).thenReturn(dto);

        PageDto result = tools.createPageManifest(dto);

        assertEquals(dto, result);
        verify(pageService).createPage(dto);
    }
}