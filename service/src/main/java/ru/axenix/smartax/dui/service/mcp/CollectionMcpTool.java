package ru.axenix.smartax.dui.service.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.util.StringUtils;
import ru.axenix.smartax.common.exception.SmartaxException;
import ru.axenix.smartax.common.model.error.SmartaxError;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.dui.service.contract.model.CollectionDto;
import ru.axenix.smartax.dui.service.contract.model.CollectionShortDto;
import ru.axenix.smartax.lib.mcp.annotation.SmartaxMcpTool;

import java.util.List;
import java.util.UUID;

/**
 * MCP-инструменты для работы с коллекциями DUI.
 */
@SmartaxMcpTool
@RequiredArgsConstructor
public class CollectionMcpTool {

    private final CollectionService collectionService;

    /**
     * Возвращает список коллекций пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сокращенный список коллекций
     */
    public List<CollectionShortDto> listCollections(
            @ToolParam(description = "Admin user id; omit to use MCP HTTP Basic user name", required = false)
            String userId
    ) {
        return collectionService.getAllCollections(resolveUserId(userId));
    }

    /**
     * Возвращает полный манифест коллекции.
     *
     * @param collectionUUID UUID коллекции
     * @return манифест коллекции
     */
    @Tool
    public CollectionDto getCollectionManifest(
            @ToolParam(description = "Collection UUID") UUID collectionUUID
    ) {
        return collectionService.getCollection(collectionUUID);
    }

    /**
     * Обновляет манифест существующей коллекции.
     *
     * @param collectionUUID UUID коллекции
     * @param collectionDto новый манифест коллекции
     * @return обновленный манифест
     */
    @Tool
    public CollectionDto saveCollectionManifest(
            @ToolParam(description = "Collection UUID") UUID collectionUUID,
            @ToolParam(description = "Collection manifest payload") CollectionDto collectionDto
    ) {
        return collectionService.editCollection(collectionUUID, collectionDto);
    }

    /**
     * Создает новую коллекцию.
     *
     * @param userId идентификатор владельца
     * @param collectionDto манифест новой коллекции
     * @return созданная коллекция
     */
    @Tool
    public CollectionDto createCollectionManifest(
            @ToolParam(description = "User identifier") String userId,
            @ToolParam(description = "Collection manifest payload") CollectionDto collectionDto
    ) {
        return collectionService.createCollection(userId, collectionDto);
    }

    /**
     * Нормализует и валидирует идентификатор пользователя.
     *
     * @param userId исходный userId
     * @return нормализованный userId
     */
    private static String resolveUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new SmartaxException("Для MCP метода listCollections необходимо передать userId",
                SmartaxError.BAD_REQUEST);
        }
        return userId.trim();
    }
}

