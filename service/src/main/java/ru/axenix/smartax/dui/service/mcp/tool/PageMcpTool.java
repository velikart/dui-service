package ru.axenix.smartax.dui.service.mcp.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;

import java.util.List;
import java.util.UUID;

/**
 * MCP-инструменты для доступа к страницам DUI.
 */
@DuiMcpTool
@RequiredArgsConstructor
public class PageMcpTool {

    private final PageService pageService;

    /**
     * Возвращает список доступных страниц.
     *
     * @return список страниц с краткой информацией
     */
    @Tool(name = "listPages", description = "List available pages: id, name, title")
    public List<PageShortDto> listPages() {
        return pageService.listPages();
    }

    /**
     * Возвращает JSON-инструкции страницы по имени.
     *
     * @param pageName техническое имя страницы
     * @return инструкции страницы в виде JSON-подобной структуры
     */
    @Tool(name = "getPageManifest", description = "Get page manifest by page name")
    public PageDto getPageManifest(
            @ToolParam(description = "Page name") String pageName
    ) {
        return pageService.getPageByName(pageName);
    }

    /**
     * Сохранение (перезапись) манифеста страницы.
     *
     * @param pageUUID идентификатор страницы.
     * @param pageDto  контент манифеста страницы.
     * @return обновленный манифест страницы.
     */
    @Tool(name = "savePageManifest", description = "Save updated page manifest by pageUUID")
    public PageDto savePageManifest(
        @ToolParam(description = "Page UUID") UUID pageUUID,
        @ToolParam(description = "Page manifest payload") PageDto pageDto
    ) {
        return pageService.editPage(pageUUID, pageDto);
    }

    /**
     * Создание нового манифеста страницы.
     *
     * @param pageDto контент манифеста страницы.
     * @return созданный манифест страницы.
     */
    @Tool(name = "createPageManifest", description = "Create page manifest")
    public PageDto createPageManifest(
        @ToolParam(description = "Page manifest payload") PageDto pageDto
    ) {
        return pageService.createPage(pageDto);
    }
}