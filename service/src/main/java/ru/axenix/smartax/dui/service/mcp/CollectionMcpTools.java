package ru.axenix.smartax.dui.service.mcp;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CollectionMcpTools implements DuiMcpToolService {

    private final CollectionService collectionService;

    @Tool(
            name = "listCollections",
            description = "List collections (uuid, title) for admin userId. If userId omitted, uses HTTP Basic login (MCP USERNAME = same as in mcp.json / MCP_USERNAME env)."
    )
    public List<CollectionShortDto> listCollections(
            @ToolParam(description = "Admin user id; omit to use MCP HTTP Basic user name", required = false) String userId
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
        if (StringUtils.hasText(userId)) {
            return userId.trim();
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new IllegalArgumentException(
                    "Укажи userId или подключи MCP с HTTP Basic (USERNAME в mcp.json = userId в БД; на сервере MCP_USERNAME/MCP_PASSWORD)"
            );
        }
        return auth.getName();
    }
}

