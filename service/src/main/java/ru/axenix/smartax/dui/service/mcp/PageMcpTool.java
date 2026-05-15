package ru.axenix.smartax.dui.service.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import ru.axenix.smartax.dui.service.application.page.service.PageService;
import ru.axenix.smartax.dui.service.contract.model.PageDto;
import ru.axenix.smartax.dui.service.contract.model.PageShortDto;
import ru.axenix.smartax.lib.mcp.annotation.SmartaxMcpTool;

import java.util.List;
import java.util.UUID;

/**
 * MCP-инструменты для доступа к страницам DUI.
 */
@SmartaxMcpTool
@RequiredArgsConstructor
public class PageMcpTool {

    private final PageService pageService;

    /**
     * Возвращает список доступных страниц.
     *
     * @return список страниц с краткой информацией
     */
    @Tool
    public List<PageShortDto> listPages() {
        return pageService.listPages();
    }

    /**
     * Возвращает JSON-инструкции страницы по имени.
     *
     * @param pageName техническое имя страницы
     * @return инструкции страницы в виде JSON-подобной структуры
     */
    @Tool
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
    @Tool
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
    @Tool
    public PageDto createPageManifest(
        @ToolParam(description = "Page manifest payload") PageDto pageDto
    ) {
        return pageService.createPage(pageDto);
    }
}