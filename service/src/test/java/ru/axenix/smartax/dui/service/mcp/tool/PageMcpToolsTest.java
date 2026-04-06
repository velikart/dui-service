package ru.axenix.smartax.dui.service.mcp.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.axenix.smartax.dui.service.application.page.model.PageShortDto;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageMcpToolsTest {

    @Mock
    private PageService pageService;

    @Test
    void listPages_ReturnsPageShortDtoList() {
        PageMcpTools tools = new PageMcpTools(pageService);
        PageShortDto page = new PageShortDto(UUID.randomUUID(), "dashboard", "Dashboard");
        when(pageService.listPages()).thenReturn(List.of(page));

        List<PageShortDto> result = tools.listPages();

        assertEquals(1, result.size());
        assertEquals("dashboard", result.get(0).name());
        assertEquals("Dashboard", result.get(0).title());
        verify(pageService).listPages();
    }

    @Test
    void getPageInstructions_ReturnsInstructionsFromPageService() {
        PageMcpTools tools = new PageMcpTools(pageService);
        PageDto dto = new PageDto();
        dto.setInstructions(Map.of("key", "value"));
        when(pageService.getPageByName("dashboard")).thenReturn(dto);

        Map<String, Object> result = tools.getPageInstructions("dashboard");

        assertEquals("value", result.get("key"));
        verify(pageService).getPageByName("dashboard");
    }
}
