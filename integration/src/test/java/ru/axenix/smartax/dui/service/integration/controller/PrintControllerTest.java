package ru.axenix.smartax.dui.service.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.axenix.smartax.dui.service.integration.dto.TemplateDto;
import ru.axenix.smartax.dui.service.integration.dto.TemplateRequest;
import ru.axenix.smartax.dui.service.integration.mapper.Base64FileMapper;
import ru.axenix.smartax.dui.service.integration.mapper.TemplateMapper;
import ru.axenix.smartax.dui.service.integration.mapper.TemplateTableMapper;
import ru.axenix.smartax.dui.service.model.table.FileResponseDto;
import ru.axenix.smartax.dui.service.model.table.TableResponse;
import ru.axenix.smartax.printservice.client.TemplateServiceClient;
import ru.axenix.smartax.printservice.model.templateview.create.CreateTemplateRequestDto;
import ru.axenix.smartax.printservice.model.templateview.find.FindTemplateRequestDto;
import ru.axenix.smartax.printservice.model.templateview.find.FindTemplateResponseDto;
import ru.axenix.smartax.printservice.model.templateview.find.TemplateMeta;
import ru.axenix.smartax.printservice.model.templateview.update.UpdateTemplateByIdRequestDto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class PrintControllerTest {

    private static final String TEMPLATE_ID_VALUE = "00000000-0000-0000-0000-000000000001";
    private static final UUID TEMPLATE_ID = UUID.fromString(TEMPLATE_ID_VALUE);

    private static final String BASE64_CONTENT = "base64";
    private static final String TEST_FILE_NAME = "test.txt";
    private static final String TEMPLATE_DESCRIPTION = "DESC";
    private static final String TEMPLATE_CODE = "CODE";
    private static final String DOWNLOADED_FILE_NAME = "file.bin";

    @Mock
    private TemplateServiceClient templateServiceClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TemplateTableMapper templateTableMapper;

    @Mock
    private Base64FileMapper base64FileMapper;

    @Mock
    private TemplateMapper templateMapper;

    @InjectMocks
    private PrintController printController;

    @Test
    void testGetTemplates() {
        FindTemplateResponseDto findResponse = new FindTemplateResponseDto();
        when(templateServiceClient.findTemplates(any(FindTemplateRequestDto.class))).thenReturn(findResponse);

        TableResponse tableResponse = new TableResponse();
        when(templateTableMapper.toTableResponse(findResponse)).thenReturn(tableResponse);

        TableResponse result = printController.getTemplates();

        assertEquals(tableResponse, result);
        verify(templateServiceClient).findTemplates(any(FindTemplateRequestDto.class));
        verify(templateTableMapper).toTableResponse(findResponse);
    }

    @Test
    void testGetTemplateFound() {
        TemplateMeta templateMeta = new TemplateMeta();
        templateMeta.setTemplateId(TEMPLATE_ID);

        FindTemplateResponseDto findResponse = new FindTemplateResponseDto();
        findResponse.setTemplates(List.of(templateMeta));

        when(templateServiceClient.findTemplates(any(FindTemplateRequestDto.class))).thenReturn(findResponse);

        byte[] fileContent = "content".getBytes(StandardCharsets.UTF_8);
        when(templateServiceClient.downloadTemplate(TEMPLATE_ID)).thenReturn(fileContent);

        // мокаем поведение маппера: переносим templateId и сам FileResponseDto
        when(templateMapper.toDto(any(TemplateMeta.class), any(FileResponseDto.class)))
                .thenAnswer(invocation -> {
                    TemplateMeta metaArg = invocation.getArgument(0);
                    FileResponseDto fileArg = invocation.getArgument(1);
                    TemplateDto dto = new TemplateDto();
                    dto.setTemplateId(metaArg.getTemplateId());
                    dto.setFile(fileArg);
                    return dto;
                });

        TemplateDto result = printController.getTemplate(TEMPLATE_ID);

        assertNotNull(result);
        assertEquals(TEMPLATE_ID, result.getTemplateId());
        assertNotNull(result.getFile());
        assertEquals(TEMPLATE_ID.toString(), result.getFile().getUuid());
        assertEquals(DOWNLOADED_FILE_NAME, result.getFile().getName());
        assertNotNull(result.getFile().getContent());

        verify(templateMapper).toDto(any(TemplateMeta.class), any(FileResponseDto.class));
    }

    @Test
    void testGetTemplateNotFound() {
        FindTemplateResponseDto findResponse = new FindTemplateResponseDto();
        findResponse.setTemplates(Collections.emptyList());

        when(templateServiceClient.findTemplates(any(FindTemplateRequestDto.class))).thenReturn(findResponse);
        when(templateServiceClient.downloadTemplate(TEMPLATE_ID)).thenReturn(new byte[0]);
        when(templateMapper.toDto(isNull(), any(FileResponseDto.class))).thenReturn(null);

        TemplateDto result = printController.getTemplate(TEMPLATE_ID);

        assertNull(result);
        verify(templateMapper).toDto(isNull(), any(FileResponseDto.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetTemplateSchema() {
        Map<String, Object> schemaItem = new HashMap<>();
        schemaItem.put("key", "value");

        TemplateMeta templateMeta = new TemplateMeta();
        templateMeta.setTemplateId(TEMPLATE_ID);
        templateMeta.setSchema(Map.of("field1", schemaItem));

        FindTemplateResponseDto findResponse = new FindTemplateResponseDto();
        findResponse.setTemplates(List.of(templateMeta));

        when(templateServiceClient.findTemplates(any(FindTemplateRequestDto.class))).thenReturn(findResponse);

        Map<String, Object> convertedMap = Map.of("converted", "value");
        doReturn(convertedMap)
                .when(objectMapper)
                .convertValue(any(), any(TypeReference.class));

        TableResponse result = printController.getTemplateSchema(TEMPLATE_ID);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(convertedMap, result.getRecords().get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetTemplateSchemaPagination() {
        Map<String, Object> schemaMap = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            schemaMap.put("field" + i, new Object());
        }

        TemplateMeta templateMeta = new TemplateMeta();
        templateMeta.setTemplateId(TEMPLATE_ID);
        templateMeta.setSchema(schemaMap);

        FindTemplateResponseDto findResponse = new FindTemplateResponseDto();
        findResponse.setTemplates(List.of(templateMeta));

        when(templateServiceClient.findTemplates(any(FindTemplateRequestDto.class))).thenReturn(findResponse);

        Map<String, Object> converted = Map.of("k", "v");
        doReturn(converted)
                .when(objectMapper)
                .convertValue(any(), any(TypeReference.class));

        TableResponse result = printController.getTemplateSchema(TEMPLATE_ID);

        assertEquals(6, result.getTotal());
        assertEquals(5, result.getRecords().size());
    }

    @Test
    void testCreateTemplate() throws IOException {
        TemplateRequest request = new TemplateRequest();
        request.setTemplateCode(TEMPLATE_CODE);
        request.setDescription(TEMPLATE_DESCRIPTION);

        FileResponseDto fileDto = new FileResponseDto();
        fileDto.setContent(BASE64_CONTENT);
        fileDto.setName(TEST_FILE_NAME);
        request.setFile(fileDto);

        File mockFile = mock(File.class);
        when(base64FileMapper.map(BASE64_CONTENT, TEST_FILE_NAME)).thenReturn(mockFile);

        printController.createTemplate(request);

        verify(base64FileMapper).map(BASE64_CONTENT, TEST_FILE_NAME);
        verify(templateServiceClient).createTemplate(any(CreateTemplateRequestDto.class), eq(mockFile));
    }

    @Test
    void testUpdateTemplate() throws IOException {
        FileResponseDto fileDto = new FileResponseDto();
        fileDto.setContent(BASE64_CONTENT);
        fileDto.setName(TEST_FILE_NAME);

        TemplateDto request = TemplateDto.builder()
                .description(TEMPLATE_DESCRIPTION)
                .file(fileDto)
                .build();

        File mockFile = mock(File.class);
        when(base64FileMapper.map(BASE64_CONTENT, TEST_FILE_NAME)).thenReturn(mockFile);

        printController.updateTemplate(TEMPLATE_ID, request);

        verify(base64FileMapper).map(BASE64_CONTENT, TEST_FILE_NAME);
        verify(templateServiceClient).updateTemplate(any(UpdateTemplateByIdRequestDto.class), eq(mockFile));
    }

    @Test
    void testDeleteTemplate() {
        printController.deleteTemplate(TEMPLATE_ID);
        verify(templateServiceClient).deleteTemplateById(TEMPLATE_ID);
    }

    @Test
    void testDownloadTemplate() {
        byte[] content = "content".getBytes(StandardCharsets.UTF_8);
        when(templateServiceClient.downloadTemplate(TEMPLATE_ID)).thenReturn(content);

        ResponseEntity<ByteArrayResource> response = printController.downloadTemplate(TEMPLATE_ID);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertArrayEquals(content, response.getBody().getByteArray());
    }
}
