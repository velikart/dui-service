package ru.axenix.smartax.dui.service.application.template.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;
import ru.axenix.smartax.dui.service.application.template.service.TemplateService;
import ru.axenix.smartax.dui.service.contract.model.TemplateDto;
import ru.axenix.smartax.dui.service.contract.model.TemplateFilterDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateController templateController;

    @Test
    void testGetTemplates() {
        TemplateFilterDto filter = new TemplateFilterDto();
        List<TemplateDto> expectedTemplates = Collections.singletonList(new TemplateDto());
        Mockito.when(templateService.getTemplates(filter)).thenReturn(expectedTemplates);

        List<TemplateDto> actualTemplates = templateController.getTemplates(filter);

        Assertions.assertEquals(expectedTemplates, actualTemplates);
        Mockito.verify(templateService, Mockito.times(1)).getTemplates(filter);
    }

    @Test
    void testGetTemplatePage() {
        UUID uuid = UUID.randomUUID();
        Resource expectedResource = Mockito.mock(Resource.class);
        Mockito.when(templateService.getTemplatePage(uuid)).thenReturn(expectedResource);

        Resource actualResource = templateController.getTemplatePage(uuid);

        Assertions.assertEquals(expectedResource, actualResource);
        Mockito.verify(templateService, Mockito.times(1)).getTemplatePage(uuid);
    }

    @Test
    void testGetTemplateImage() {
        UUID uuid = UUID.randomUUID();
        String filename = "test-image.png";
        Resource resource = Mockito.mock(Resource.class);
        FileDto fileDto = new FileDto(filename, resource);
        Mockito.when(templateService.getTemplateImage(uuid)).thenReturn(fileDto);

        Resource actualResource = templateController.getTemplateImage(uuid);

        Assertions.assertNotNull(actualResource);
        Assertions.assertEquals(resource, actualResource);
        Mockito.verify(templateService, Mockito.times(1)).getTemplateImage(uuid);
    }
}