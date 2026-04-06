package ru.axenix.smartax.dui.service.mcp.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.dui.service.contract.model.CollectionShortDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionMcpToolTest {

    @Mock
    private CollectionService collectionService;

    @Test
    void listCollections_UsesProvidedUserId() {
        CollectionMcpTool tools = new CollectionMcpTool(collectionService);
        when(collectionService.getAllCollections("admin")).thenReturn(List.of(new CollectionShortDto()));

        List<CollectionShortDto> result = tools.listCollections("admin");

        assertEquals(1, result.size());
        verify(collectionService).getAllCollections("admin");
    }

    @Test
    void listCollections_ThrowsWhenUserIdNotProvided() {
        CollectionMcpTool tools = new CollectionMcpTool(collectionService);

        assertThrows(IllegalArgumentException.class, () -> tools.listCollections("  "));
    }
}