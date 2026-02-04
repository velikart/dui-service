package ru.axenix.smartax.dui.service.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.integration.dto.TemplateRequest;
import ru.axenix.smartax.dui.service.integration.dto.TemplateDto;
import ru.axenix.smartax.dui.service.integration.mapper.Base64FileMapper;
import ru.axenix.smartax.dui.service.integration.mapper.TemplateMapper;
import ru.axenix.smartax.dui.service.model.table.FileResponseDto;
import ru.axenix.smartax.dui.service.integration.mapper.TemplateTableMapper;
import ru.axenix.smartax.dui.service.model.table.TableResponse;
import ru.axenix.smartax.printservice.client.TemplateServiceClient;
import ru.axenix.smartax.printservice.model.templateview.create.CreateTemplateRequestDto;
import ru.axenix.smartax.printservice.model.templateview.find.FindTemplateRequestDto;
import ru.axenix.smartax.printservice.model.templateview.find.FindTemplateResponseDto;
import ru.axenix.smartax.printservice.model.templateview.find.TemplateMeta;
import ru.axenix.smartax.printservice.model.templateview.update.UpdateTemplateByIdRequestDto;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 * Контроллер для взаимодействия DUI с print-service.
 *
 * @author Velikanov Artyom.
 */
@RestController
@RequestMapping("/app/v1/print/template")
@RequiredArgsConstructor
@Tag(name = "Интеграция DUI c print-service", description = "Интеграция страниц DUI с сервисом печатных форм (print-service)")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class PrintController {

    private static final String FILENAME = "file.bin";
    private static final int LIMIT_SCHEMA = 5;

    private final TemplateServiceClient templateServiceClient;

    private final ObjectMapper objectMapper;
    private final TemplateTableMapper templateTableMapper;
    private final Base64FileMapper base64FileMapper;
    private final TemplateMapper templateMapper;

    /**
     * Получение списка шаблонов печатной формы с фильтрацией, сортировкой и пагинацией.
     *
     * @return Список шаблонов печатной формы
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Получение списка шаблонов печатной формы",
            description = "Получение списка шаблонов печатной формы с фильтрацией, сортировкой и пагинацией",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/filter")
    public TableResponse getTemplates() {
        FindTemplateResponseDto response = templateServiceClient.findTemplates(new FindTemplateRequestDto());

        return templateTableMapper.toTableResponse(response);
    }

    /**
     * Получение шаблона печатной формы.
     *
     * @param templateId UUID шаблона печатной формы.
     * @return шаблон печатной формы.
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Получение шаблона печатной формы",
            description = "Получение шаблона печатной формы",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/{templateId}")
    public TemplateDto getTemplate(@PathVariable("templateId") UUID templateId) {
        TemplateMeta response = Optional.ofNullable(templateServiceClient.findTemplates(new FindTemplateRequestDto()))
                .map(FindTemplateResponseDto::getTemplates)
                .stream()
                .flatMap(List::stream)
                .filter(t -> templateId.equals(t.getTemplateId()))
                .findFirst()
                .orElse(null);
        byte[] data = templateServiceClient.downloadTemplate(templateId);

        FileResponseDto file = new FileResponseDto(
                templateId.toString(),
                FILENAME,
                Base64.getEncoder().encodeToString(data)
        );
        return templateMapper.toDto(response, file);
    }

    /**
     * Получение схемы плейсхолдеров шаблона печатной формы
     *
     * @param templateId UUID шаблона печатной формы.
     * @return таблица с плейсхолдерами.
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Получение схемы плейсхолдеров шаблона печатной формы",
            description = "Получение схемы плейсхолдеров шаблона печатной формы",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/{templateId}/schema")
    public TableResponse getTemplateSchema(@PathVariable("templateId") UUID templateId) {

        var result = Optional.ofNullable(templateServiceClient.findTemplates(new FindTemplateRequestDto()))
                .map(FindTemplateResponseDto::getTemplates)
                .stream()
                .flatMap(List::stream)
                .filter(t -> templateId.equals(t.getTemplateId()))
                .findFirst()
                .map(TemplateMeta::getSchema)
                .map(Map::values)
                .stream()
                .flatMap(Collection::stream)
                .map(v -> objectMapper.convertValue(
                        v, new TypeReference<Map<String, Object>>() { }))
                .toList();

        int total = result.size();
        List<Map<String, Object>> page = total > LIMIT_SCHEMA ? result.subList(0, LIMIT_SCHEMA) : result;

        return new TableResponse(total, page);
    }

    /**
     * Создание печатной формы.
     *
     * @param request модель запроса на создание.
     * @throws IOException ошибка обработки контента файла.
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Создание печатной формы",
            description = "Создание печатной формы",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping
    public void createTemplate(@RequestBody TemplateRequest request) throws IOException {
        File tempFile = base64FileMapper.map(
                request.getFile().getContent(),
                request.getFile().getName()
        );

        CreateTemplateRequestDto requestPrint = new CreateTemplateRequestDto();
        requestPrint.setDescription(request.getDescription());
        requestPrint.setTemplateCode(request.getTemplateCode());

        templateServiceClient.createTemplate(
                requestPrint,
                tempFile
        );
    }

    /**
     * Обновление печатной формы.
     *
     * @param templateId идентификатор шаблона.
     * @param request модель запроса на создание.
     * @throws IOException ошибка обработки контента файла.
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Обновление печатной формы",
            description = "Обновление печатной формы",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PutMapping("/{templateId}")
    public void updateTemplate(@PathVariable("templateId") UUID templateId, @RequestBody TemplateDto request) throws IOException {
        File tempFile = base64FileMapper.map(
                request.getFile().getContent(),
                request.getFile().getName()
        );

        UpdateTemplateByIdRequestDto requestPrint = new UpdateTemplateByIdRequestDto();
        requestPrint.setDescription(request.getDescription());
        requestPrint.setTemplateId(templateId);

        templateServiceClient.updateTemplate(
                requestPrint,
                tempFile
        );
    }

    /**
     * Удаление шаблона печатной формы.
     *
     * @param templateId UUID шаблона печатной формы.
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Удаление шаблона печатной формы",
            description = "Удаление шаблона печатной формы",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @DeleteMapping("/{templateId}")
    public void deleteTemplate(@PathVariable("templateId") UUID templateId) {
        templateServiceClient.deleteTemplateById(templateId);
    }

    /**
     * Скачивание файла печатной формы по UUID.
     *
     * @param templateId UUID шаблона печатной формы.
     * @return файл печатной формы.
     */
    @Operation(
            tags = "Интеграция с сервисом печатных форм",
            summary = "Скачивание файла печатной формы по UUID",
            description = "Скачивание файла печатной формы по UUID",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/{templateId}/download")
    public ResponseEntity<ByteArrayResource> downloadTemplate(@PathVariable("templateId") UUID templateId) {
        byte[] data = templateServiceClient.downloadTemplate(templateId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

}