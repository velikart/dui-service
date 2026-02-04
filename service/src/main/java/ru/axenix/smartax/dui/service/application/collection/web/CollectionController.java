package ru.axenix.smartax.dui.service.application.collection.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления коллекциями администратора.
 *
 * @author Velikanov Artyom.
 */
@RestController
@RequestMapping("/app/v1")
@RequiredArgsConstructor
@Tag(name = "Коллекции администратора", description = "Управление коллекциями конструирования интерфейса админом")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class CollectionController {

    private static final String USER_ID = "fd1eb78d-1e09-4ee3-98db-5469108cd7fe";

    private final CollectionService collectionService;


    @Operation(
            tags = "Коллекции администратора",
            summary = "Получение списка коллекций администратора",
            description = "Получение списка коллекций администратора по его ID из контекста авторизации",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/collection/list")
    public List<CollectionShortDto> getAllCollections() {
        return collectionService.getAllCollections(USER_ID);
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Получение коллекции администратора по идентификатору",
            description = "Получение коллекции администратора по идентификатору UUID",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/collection/{collectionUUID}")
    public CollectionDto getCollection(@PathVariable UUID collectionUUID) {
        return collectionService.getCollection(collectionUUID);
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Получение коллекции администратора из истории изменений",
            description = "Получение коллекции администратора из истории изменений по UUID истории",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/collection/{collectionUUID}/history/{historyUUID}")
    public CollectionDto getCollectionByHistory(@PathVariable UUID collectionUUID,
                                       @PathVariable UUID historyUUID) {
        return collectionService.getCollectionByHistoryUUID(historyUUID);
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Экспорт коллекции администратора",
            description = "Экспорт коллекции администратора в формате .json файла",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @GetMapping("/collection/{collectionUUID}/export")
    public ResponseEntity<Resource> exportCollection(@PathVariable UUID collectionUUID) {
        var dto = collectionService.exportCollection(collectionUUID);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + dto.getFilename() + ".json\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(dto.getFile());
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Получение истории изменений коллекции администратора",
            description = "Получение списка коллекций в истории изменений по UUID коллекции",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/collection/{collectionUUID}/history")
    public List<CollectionHistoryDto> getCollectionHistory(@PathVariable UUID collectionUUID) {
        return collectionService.getCollectionHistory(collectionUUID);
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Создание коллекции администратора",
            description = "Создание коллекции администратора со страницами и моками-объектов",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/collection")
    public CollectionDto createCollection(@RequestBody CollectionDto collection) {
        return collectionService.createCollection(USER_ID, collection);
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Редактирование коллекций администратора",
            description = "Редактирование коллекций администратора",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PutMapping("/collection/{collectionUUID}")
    public CollectionDto editCollection(@PathVariable UUID collectionUUID,
                                        @RequestBody CollectionDto collection) {
        return collectionService.editCollection(collectionUUID, collection);
    }

    @Operation(
            tags = "Коллекции администратора",
            summary = "Удаление коллекции администратора",
            description = "Удаление коллекции администратора по UUID",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @DeleteMapping("/collection/{collectionUUID}")
    public void deleteCollection(@PathVariable UUID collectionUUID) {
        collectionService.deleteCollection(collectionUUID);
    }
}
