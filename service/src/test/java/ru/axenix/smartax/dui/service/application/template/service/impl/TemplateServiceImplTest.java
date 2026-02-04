package ru.axenix.smartax.dui.service.application.template.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.axenix.smartax.dui.service.application.template.domain.TemplateEntity;
import ru.axenix.smartax.dui.service.application.template.domain.TemplateRepository;
import ru.axenix.smartax.dui.service.error.ApplicationException;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateFilterDto;
import ru.axenix.smartax.dui.service.application.template.model.TemplateType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private TemplateEntity templateEntity;
    private UUID templateUuid;

    @BeforeEach
    void setUp() {
        templateUuid = UUID.randomUUID();
        templateEntity = TemplateEntity.builder()
                .uuid(templateUuid)
                .title("Test Template")
                .filenamePage("test.json")
                .filenameImage("test.png")
                .type(TemplateType.PAGE)
                .build();
    }

    @Test
    @DisplayName("getTemplates should return all templates when filter is null")
    void test_get_templates_with_null_filter() {
        when(templateRepository.findAll()).thenReturn(List.of(templateEntity));

        List<TemplateDto> result = templateService.getTemplates(null);

        assertEquals(1, result.size());
        assertEquals(templateEntity.getUuid(), result.getFirst().getUuid());
        verify(templateRepository, times(1)).findAll();
        verify(templateRepository, never()).findAllByType(any());
    }

    @Test
    @DisplayName("getTemplates should return all templates when filter type is null")
    void test_get_templates_with_null_filter_type() {
        TemplateFilterDto filter = new TemplateFilterDto(null);
        when(templateRepository.findAll()).thenReturn(List.of(templateEntity));

        List<TemplateDto> result = templateService.getTemplates(filter);

        assertEquals(1, result.size());
        assertEquals(templateEntity.getUuid(), result.getFirst().getUuid());
        verify(templateRepository, times(1)).findAll();
        verify(templateRepository, never()).findAllByType(any());
    }

    @Test
    @DisplayName("getTemplates should return filtered templates when a valid filter type is provided")
    void test_get_templates_with_valid_filter_type() {
        TemplateFilterDto filter = new TemplateFilterDto(TemplateType.PAGE);
        when(templateRepository.findAllByType(TemplateType.PAGE)).thenReturn(List.of(templateEntity));

        List<TemplateDto> result = templateService.getTemplates(filter);

        assertEquals(1, result.size());
        assertEquals(templateEntity.getUuid(), result.getFirst().getUuid());
        verify(templateRepository, never()).findAll();
        verify(templateRepository, times(1)).findAllByType(TemplateType.PAGE);
    }

    @Test
    @DisplayName("getTemplatePage should return resource for a valid template UUID")
    void test_get_template_page_with_valid_uuid() {
        when(templateRepository.findById(templateUuid)).thenReturn(Optional.of(templateEntity));

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(true))) {
            Resource result = templateService.getTemplatePage(templateUuid);

            assertNotNull(result);
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    @DisplayName("getTemplatePage should throw TEMPLATE_NOT_FOUND for an invalid UUID")
    void test_get_template_page_with_invalid_uuid() {
        when(templateRepository.findById(templateUuid)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> templateService.getTemplatePage(templateUuid));
    }

    @Test
    @DisplayName("getTemplatePage should throw TEMPLATE_PAGE_NOT_FOUND when resource does not exist")
    void test_get_template_page_with_non_existent_resource() {
        when(templateRepository.findById(templateUuid)).thenReturn(Optional.of(templateEntity));

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(false))) {
            assertThrows(ApplicationException.class, () -> templateService.getTemplatePage(templateUuid));
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    @DisplayName("getTemplateImage should return FileDto for a valid template UUID")
    void test_get_template_image_with_valid_uuid() {
        when(templateRepository.findById(templateUuid)).thenReturn(Optional.of(templateEntity));

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(true))) {
            FileDto result = templateService.getTemplateImage(templateUuid);

            assertNotNull(result);
            assertEquals(templateEntity.getFilenameImage(), result.getFilename());
            assertNotNull(result.getFile());
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    @DisplayName("getTemplateImage should throw TEMPLATE_NOT_FOUND for an invalid UUID")
    void test_get_template_image_with_invalid_uuid() {
        when(templateRepository.findById(templateUuid)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> templateService.getTemplateImage(templateUuid));
    }

    @Test
    @DisplayName("getTemplateImage should throw TEMPLATE_IMAGE_NOT_FOUND when resource does not exist")
    void test_get_template_image_with_non_existent_resource() {
        when(templateRepository.findById(templateUuid)).thenReturn(Optional.of(templateEntity));

        try (MockedConstruction<ClassPathResource> mocked = mockConstruction(ClassPathResource.class,
                (mock, context) -> when(mock.exists()).thenReturn(false))) {
            assertThrows(ApplicationException.class, () -> templateService.getTemplateImage(templateUuid));
            assertEquals(1, mocked.constructed().size());
        }
    }
}
