package ru.axenix.smartax.dui.service.mcp.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import ru.axenix.smartax.dui.service.application.template.service.TemplateService;
import ru.axenix.smartax.dui.service.contract.model.TemplateDto;
import ru.axenix.smartax.dui.service.contract.model.TemplateFilterDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@DuiMcpTool
@RequiredArgsConstructor
public class TemplateMcpTool {

    private final TemplateService templateService;

    @Tool(name = "listTemplates", description = "List page templates. Optional type filter narrows the result")
    public List<TemplateDto> listTemplates(
            @ToolParam(description = "Template type filter. Optional", required = false) TemplateFilterDto.TypeEnum type
    ) {
        TemplateFilterDto filter = null;
        if (type != null) {
            filter = new TemplateFilterDto();
            filter.setType(type);
        }
        return templateService.getTemplates(filter);
    }

    @Tool(name = "getTemplatePageJson", description = "Get template page JSON by template UUID")
    public String getTemplatePageJson(
            @ToolParam(description = "Template UUID") UUID templateUUID
    ) {
        return readResourceAsUtf8(templateService.getTemplatePage(templateUUID));
    }

    @Tool(name = "getTemplateImageName", description = "Get template image filename by template UUID")
    public String getTemplateImageName(
            @ToolParam(description = "Template UUID") UUID templateUUID
    ) {
        return templateService.getTemplateImage(templateUUID).getFilename();
    }

    private String readResourceAsUtf8(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать ресурс шаблона", e);
        }
    }
}