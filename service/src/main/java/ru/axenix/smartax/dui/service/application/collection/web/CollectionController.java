package ru.axenix.smartax.dui.service.application.collection.web;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.common.security.SecurityContext;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.dui.service.contract.api.CollectionsApi;
import ru.axenix.smartax.dui.service.contract.model.CollectionDto;
import ru.axenix.smartax.dui.service.contract.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.contract.model.CollectionShortDto;
import ru.axenix.smartax.lib.mcp.annotation.SmartaxMcpTool;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления коллекциями администратора.
 */
@SmartaxMcpTool
@RestController
@RequiredArgsConstructor
public class CollectionController implements CollectionsApi {

    private final CollectionService collectionService;

    @Tool
    @Override
    @Authorization
    public List<CollectionShortDto> getAllCollections() {
        return collectionService.getAllCollections(SecurityContext.getUserInfoOrEmpty().getId());
    }

    @Override
    @Authorization
    public CollectionDto getCollection(UUID collectionUUID) {
        return collectionService.getCollection(collectionUUID);
    }

    @Override
    @Authorization
    public CollectionDto getCollectionByHistory(UUID collectionUUID, UUID historyUUID) {
        return collectionService.getCollectionByHistoryUUID(historyUUID);
    }

    @Override
    @Authorization
    public Resource exportCollection(UUID collectionUUID) {
        return collectionService.exportCollection(collectionUUID).getFile();
    }

    @Override
    @Authorization
    public List<CollectionHistoryDto> getCollectionHistory(UUID collectionUUID) {
        return collectionService.getCollectionHistory(collectionUUID);
    }

    @Override
    @Authorization
    public CollectionDto createCollection(CollectionDto collection) {
        return collectionService.createCollection(SecurityContext.getUserInfoOrEmpty().getId(), collection);
    }

    @Override
    @Authorization
    public CollectionDto editCollection(UUID collectionUUID, CollectionDto collection) {
        return collectionService.editCollection(collectionUUID, collection);
    }

    @Override
    @Authorization
    public void deleteCollection(UUID collectionUUID) {
        collectionService.deleteCollection(collectionUUID);
    }
}