package ru.axenix.smartax.dui.service.application.collection.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.axenix.smartax.common.security.SecurityContext;
import ru.axenix.smartax.common.security.UserInfo;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Контроллер для управления коллекциями администратора")
class CollectionControllerTest {

    @Mock
    private CollectionService collectionService;

    @InjectMocks
    private CollectionController collectionController;

    @Test
    @DisplayName("Получение списка коллекций администратора")
    void testGetAllCollections() {
        String userId = "fd1eb78d-1e09-4ee3-98db-5469108cd7fe";
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        List<CollectionShortDto> expectedCollections =
                Collections.singletonList(new CollectionShortDto());

        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            when(collectionService.getAllCollections(userId)).thenReturn(expectedCollections);

            List<CollectionShortDto> actualCollections = collectionController.getAllCollections();

            assertNotNull(actualCollections);
            assertEquals(expectedCollections, actualCollections);
            verify(collectionService).getAllCollections(userId);
        }
    }

    @Test
    @DisplayName("Получение коллекции по UUID")
    void testGetCollection() {
        UUID collectionUUID = UUID.randomUUID();
        CollectionDto expectedDto = new CollectionDto();
        when(collectionService.getCollection(collectionUUID)).thenReturn(expectedDto);

        CollectionDto actualDto = collectionController.getCollection(collectionUUID);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(collectionService).getCollection(collectionUUID);
    }

    @Test
    @DisplayName("Получение коллекции из истории по UUID")
    void testGetCollectionByHistory() {
        UUID collectionUUID = UUID.randomUUID();
        UUID historyUUID = UUID.randomUUID();
        CollectionDto expectedDto = new CollectionDto();
        when(collectionService.getCollectionByHistoryUUID(historyUUID)).thenReturn(expectedDto);

        CollectionDto actualDto = collectionController.getCollectionByHistory(collectionUUID, historyUUID);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(collectionService).getCollectionByHistoryUUID(historyUUID);
    }

    @Test
    @DisplayName("Экспорт коллекции")
    void testExportCollection() {
        UUID collectionUUID = UUID.randomUUID();
        String filename = "test-collection";
        Resource resource = new ByteArrayResource("test data".getBytes(StandardCharsets.UTF_8));
        FileDto fileDto = new FileDto(filename, resource);

        when(collectionService.exportCollection(collectionUUID)).thenReturn(fileDto);

        ResponseEntity<Resource> response = collectionController.exportCollection(collectionUUID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource, response.getBody());
        assertEquals("application/json", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("Content-Disposition",
                response.getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS));
        assertEquals("attachment; filename=\"" + filename + ".json\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        verify(collectionService).exportCollection(collectionUUID);
    }

    @Test
    @DisplayName("Получение истории изменений коллекции")
    void testGetCollectionHistory() {
        UUID collectionUUID = UUID.randomUUID();
        List<CollectionHistoryDto> expectedHistory =
                Collections.singletonList(new CollectionHistoryDto());
        when(collectionService.getCollectionHistory(collectionUUID)).thenReturn(expectedHistory);

        List<CollectionHistoryDto> actualHistory = collectionController.getCollectionHistory(collectionUUID);

        assertNotNull(actualHistory);
        assertEquals(expectedHistory, actualHistory);
        verify(collectionService).getCollectionHistory(collectionUUID);
    }

    @Test
    @DisplayName("Создание коллекции")
    void testCreateCollection() {
        String userId = "fd1eb78d-1e09-4ee3-98db-5469108cd7fe";
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        CollectionDto collectionToCreate = new CollectionDto();
        CollectionDto expectedCollection = new CollectionDto();

        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            when(collectionService.createCollection(userId, collectionToCreate)).thenReturn(expectedCollection);

            CollectionDto actualCollection = collectionController.createCollection(collectionToCreate);

            assertNotNull(actualCollection);
            assertEquals(expectedCollection, actualCollection);
            verify(collectionService).createCollection(userId, collectionToCreate);
        }
    }

    @Test
    @DisplayName("Редактирование коллекции")
    void testEditCollection() {
        UUID collectionUUID = UUID.randomUUID();
        CollectionDto collectionToUpdate = new CollectionDto();
        CollectionDto expectedCollection = new CollectionDto();
        when(collectionService.editCollection(collectionUUID, collectionToUpdate)).thenReturn(expectedCollection);

        CollectionDto actualCollection = collectionController.editCollection(collectionUUID, collectionToUpdate);

        assertNotNull(actualCollection);
        assertEquals(expectedCollection, actualCollection);
        verify(collectionService).editCollection(collectionUUID, collectionToUpdate);
    }

    @Test
    @DisplayName("Удаление коллекции")
    void testDeleteCollection() {
        UUID collectionUUID = UUID.randomUUID();

        collectionController.deleteCollection(collectionUUID);

        verify(collectionService, times(1)).deleteCollection(collectionUUID);
    }
}
