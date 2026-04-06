package ru.axenix.smartax.dui.service.mcp.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;
import ru.axenix.smartax.dui.service.application.template.service.TemplateService;
import ru.axenix.smartax.dui.service.contract.model.TemplateDto;
import ru.axenix.smartax.dui.service.contract.model.TemplateFilterDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateMcpToolsTest {

    @Mock
    private TemplateService templateService;

    @Test
    void listTemplates_WithoutType_PassesNullFilter() {
        TemplateMcpTools tools = new TemplateMcpTools(templateService);
        when(templateService.getTemplates(null)).thenReturn(List.of(new TemplateDto()));

        List<TemplateDto> result = tools.listTemplates(null);

        assertEquals(1, result.size());
        verify(templateService).getTemplates(null);
    }

    @Test
    void listTemplates_WithType_PassesFilter() {
        TemplateMcpTools tools = new TemplateMcpTools(templateService);
        when(templateService.getTemplates(any())).thenReturn(List.of(new TemplateDto()));

        List<TemplateDto> result = tools.listTemplates(TemplateFilterDto.TypeEnum.PAGE);

        assertEquals(1, result.size());
        verify(templateService).getTemplates(any(TemplateFilterDto.class));
    }

    @Test
    void getTemplatePageJson_ReturnsUtf8Text() {
        TemplateMcpTools tools = new TemplateMcpTools(templateService);
        UUID uuid = UUID.randomUUID();
        when(templateService.getTemplatePage(uuid))
                .thenReturn(new ByteArrayResource("{\"a\":1}".getBytes(StandardCharsets.UTF_8)));

        String result = tools.getTemplatePageJson(uuid);

        assertEquals("{\"a\":1}", result);
    }

    @Test
    void getTemplateImageName_ReturnsFilename() {
        TemplateMcpTools tools = new TemplateMcpTools(templateService);
        UUID uuid = UUID.randomUUID();
        when(templateService.getTemplateImage(uuid))
                .thenReturn(new FileDto("preview.png", new ByteArrayResource(new byte[0])));

        String result = tools.getTemplateImageName(uuid);

        assertEquals("preview.png", result);
    }
}
