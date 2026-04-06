package ru.axenix.smartax.dui.service.mcp.tool;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.util.StringUtils;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.dui.service.contract.model.CollectionDto;
import ru.axenix.smartax.dui.service.contract.model.CollectionShortDto;

import java.util.List;
import java.util.UUID;

@DuiMcpTool
@RequiredArgsConstructor
public class CollectionMcpTool {

    private final CollectionService collectionService;

    @Tool(
            name = "listCollections",
            description = "List collections (uuid, title) for admin userId. If userId omitted, uses HTTP Basic login"
                    + " (MCP USERNAME = same as in mcp.json / MCP_USERNAME env)."
    )
    public List<CollectionShortDto> listCollections(
            @ToolParam(description = "Admin user id; omit to use MCP HTTP Basic user name", required = false)
            String userId
    ) {
        return collectionService.getAllCollections(resolveUserId(userId));
    }

    @Tool(name = "getCollectionManifest", description = "Get collection manifest by collectionUUID")
    public CollectionDto getCollectionManifest(
            @ToolParam(description = "Collection UUID") UUID collectionUUID
    ) {
        return collectionService.getCollection(collectionUUID);
    }

    @Tool(name = "saveCollectionManifest", description = "Save updated collection manifest by collectionUUID")
    public CollectionDto saveCollectionManifest(
            @ToolParam(description = "Collection UUID") UUID collectionUUID,
            @ToolParam(description = "Collection manifest payload") CollectionDto collectionDto
    ) {
        return collectionService.editCollection(collectionUUID, collectionDto);
    }

    @Tool(name = "createCollectionManifest", description = "Create collection manifest for provided userId")
    public CollectionDto createCollectionManifest(
            @ToolParam(description = "User identifier") String userId,
            @ToolParam(description = "Collection manifest payload") CollectionDto collectionDto
    ) {
        return collectionService.createCollection(userId, collectionDto);
    }

    private static String resolveUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("Для MCP метода listCollections необходимо передать userId");
        }
        return userId.trim();
    }
}

