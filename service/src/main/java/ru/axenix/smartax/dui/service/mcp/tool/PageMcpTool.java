package ru.axenix.smartax.dui.service.mcp.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import ru.axenix.smartax.dui.service.application.page.model.PageShortDto;
import ru.axenix.smartax.dui.service.application.page.service.PageService;

import java.util.List;
import java.util.Map;

@DuiMcpTool
@RequiredArgsConstructor
public class PageMcpTool {

    private final PageService pageService;

    @Tool(name = "listPages", description = "List available pages: id, name, title")
    public List<PageShortDto> listPages() {
        return pageService.listPages();
    }

    @Tool(name = "getPageInstructions", description = "Get JSON instructions by page name")
    public Map<String, Object> getPageInstructions(
            @ToolParam(description = "Page name") String pageName
    ) {
        return pageService.getPageByName(pageName).getInstructions();
    }
}