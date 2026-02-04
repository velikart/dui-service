package ru.axenix.smartax.dui.service.application.collection.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import ru.axenix.smartax.dui.service.application.collection.domain.CollectionEntity;
import ru.axenix.smartax.dui.service.application.collection.domain.CollectionRepository;
import ru.axenix.smartax.dui.service.application.collection.mapper.CollectionMapper;
import ru.axenix.smartax.dui.service.error.ApplicationException;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionServiceImplTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CollectionMapper collectionMapper;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    private CollectionEntity collectionEntity;
    private CollectionDto collectionDto;
    private UUID collectionId;
    private String userId;

    @BeforeEach
    void setUp() {
        collectionId = UUID.randomUUID();
        userId = "testUser";
        collectionEntity = new CollectionEntity();
        collectionEntity.setCollectionId(collectionId);
        collectionEntity.setHistoryId(UUID.randomUUID());
        collectionEntity.setUserId(userId);
        collectionEntity.setTitle("Test Collection");
        collectionEntity.setIsCurrent(true);
        collectionEntity.setCreationDate(LocalDateTime.now());

        collectionDto = new CollectionDto();
        collectionDto.setUuid(collectionId);
        collectionDto.setTitle("Test Collection");
    }

    @Test
    void testGetAllCollections() {
        when(collectionRepository.findAllByUserIdAndIsCurrentTrue(userId))
                .thenReturn(Collections.singletonList(collectionEntity));
        CollectionShortDto shortDto = new CollectionShortDto(collectionId, "Test Collection");
        when(collectionMapper.toShortDtoList(any())).thenReturn(Collections.singletonList(shortDto));

        List<CollectionShortDto> result = collectionService.getAllCollections(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Collection", result.get(0).getTitle());
        verify(collectionRepository).findAllByUserIdAndIsCurrentTrue(userId);
    }

    @Test
    void testGetCollection() {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId))
                .thenReturn(Optional.of(collectionEntity));
        when(collectionMapper.toDto(collectionEntity)).thenReturn(collectionDto);

        CollectionDto result = collectionService.getCollection(collectionId);

        assertNotNull(result);
        assertEquals(collectionDto.getTitle(), result.getTitle());
        verify(collectionRepository).findByCollectionIdAndIsCurrentTrue(collectionId);
    }

    @Test
    void testGetCollectionNotFound() {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId))
                .thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> collectionService.getCollection(collectionId));
        verify(collectionRepository).findByCollectionIdAndIsCurrentTrue(collectionId);
    }

    @Test
    void testExportCollection() throws Exception {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId))
                .thenReturn(Optional.of(collectionEntity));
        when(collectionMapper.toDto(collectionEntity)).thenReturn(collectionDto);
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.withDefaultPrettyPrinter()).thenReturn(objectWriter);
        String json = "{\"uuid\":\"" + collectionId + "\",\"title\":\"Test Collection\"}";
        when(objectWriter.writeValueAsString(collectionDto)).thenReturn(json);

        FileDto result = collectionService.exportCollection(collectionId);

        assertNotNull(result);
        assertEquals(collectionEntity.getHistoryId().toString(), result.getFilename());
        assertTrue(result.getFile() instanceof InputStreamResource);
        verify(collectionRepository).findByCollectionIdAndIsCurrentTrue(collectionId);
    }

    @Test
    void testExportCollectionNotFound() {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId))
                .thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> collectionService.exportCollection(collectionId));
    }

    @Test
    void testExportCollectionError() throws Exception {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId))
                .thenReturn(Optional.of(collectionEntity));
        when(collectionMapper.toDto(collectionEntity)).thenReturn(collectionDto);
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.withDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(collectionDto)).thenThrow(JsonProcessingException.class);

        assertThrows(ApplicationException.class, () -> collectionService.exportCollection(collectionId));
    }

    @Test
    void testGetCollectionHistory() {
        when(collectionRepository.findAllByCollectionIdOrderByCreationDateAsc(collectionId))
                .thenReturn(Collections.singletonList(collectionEntity));
        CollectionHistoryDto historyDto = new CollectionHistoryDto();
        when(collectionMapper.toHistoryDtoList(any())).thenReturn(Collections.singletonList(historyDto));

        List<CollectionHistoryDto> result = collectionService.getCollectionHistory(collectionId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(collectionRepository).findAllByCollectionIdOrderByCreationDateAsc(collectionId);
    }

    @Test
    void testGetCollectionByHistoryUUID() {
        UUID historyId = collectionEntity.getHistoryId();
        when(collectionRepository.findById(historyId)).thenReturn(Optional.of(collectionEntity));
        when(collectionMapper.toDto(collectionEntity)).thenReturn(collectionDto);

        CollectionDto result = collectionService.getCollectionByHistoryUUID(historyId);

        assertNotNull(result);
        assertEquals(collectionDto.getTitle(), result.getTitle());
        verify(collectionRepository).findById(historyId);
    }

    @Test
    void testGetCollectionByHistoryIdNotFound() {
        UUID historyId = UUID.randomUUID();
        when(collectionRepository.findById(historyId)).thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> collectionService.getCollectionByHistoryUUID(historyId));
    }

    @Test
    void testCreateCollection() {
        when(collectionRepository.existsByTitleAndUserIdAndCollectionIdIsNot(
                collectionDto.getTitle(), userId, null)
        ).thenReturn(false);

        when(collectionMapper.toEntity(
                eq(collectionDto),
                any(UUID.class),
                isNull(),
                eq(userId)
        )).thenReturn(collectionEntity);

        when(collectionRepository.save(any(CollectionEntity.class)))
                .thenReturn(collectionEntity);

        when(collectionMapper.toDto(collectionEntity))
                .thenReturn(collectionDto);

        CollectionDto result = collectionService.createCollection(userId, collectionDto);

        assertNotNull(result);
        assertEquals(collectionDto.getTitle(), result.getTitle());
        verify(collectionRepository).save(any(CollectionEntity.class));
        verify(collectionMapper).toEntity(eq(collectionDto), any(UUID.class), isNull(), eq(userId));
    }

    @Test
    void testCreateCollectionWithDuplicateTitle() {
        when(collectionRepository.existsByTitleAndUserIdAndCollectionIdIsNot(collectionDto.getTitle(), userId, null))
                .thenReturn(true);

        assertThrows(ApplicationException.class, () -> collectionService.createCollection(userId, collectionDto));
        verify(collectionRepository, never()).save(any(CollectionEntity.class));
    }

    @Test
    void testCreateCollectionError() {
        when(collectionRepository.existsByTitleAndUserIdAndCollectionIdIsNot(collectionDto.getTitle(), userId, null))
                .thenReturn(false);
        when(collectionMapper.toEntity(collectionDto)).thenReturn(collectionEntity);
        when(collectionRepository.save(any(CollectionEntity.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(ApplicationException.class, () -> collectionService.createCollection(userId, collectionDto));
    }

    @Test
    void testEditCollection() {
        collectionEntity.setUserId(userId);
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId))
                .thenReturn(Optional.of(collectionEntity));

        when(collectionRepository.existsByTitleAndUserIdAndCollectionIdIsNot(
                collectionDto.getTitle(),
                userId,
                collectionId)
        ).thenReturn(false);

        CollectionEntity newEntity = new CollectionEntity();
        newEntity.setCollectionId(collectionId);
        newEntity.setUserId(userId);
        newEntity.setTitle("New Title");

        when(collectionMapper.toEntity(
                eq(collectionDto),
                any(UUID.class),
                eq(collectionId),
                eq(userId)
        )).thenReturn(newEntity);

        when(collectionRepository.save(any(CollectionEntity.class)))
                .thenReturn(collectionEntity)
                .thenReturn(newEntity);

        when(collectionMapper.toDto(newEntity)).thenReturn(collectionDto);

        CollectionDto result = collectionService.editCollection(collectionId, collectionDto);

        assertNotNull(result);
        assertNull(collectionEntity.getIsCurrent());
        verify(collectionRepository, times(2)).save(any(CollectionEntity.class));
        verify(collectionMapper).toEntity(eq(collectionDto), any(UUID.class), eq(collectionId), eq(userId));
    }


    @Test
    void testEditCollectionNotFound() {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> collectionService.editCollection(collectionId, collectionDto));
        verify(collectionRepository, never()).save(any(CollectionEntity.class));
    }

    @Test
    void testEditCollectionWithDuplicateTitle() {
        when(collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionId)).thenReturn(Optional.of(collectionEntity));
        when(collectionRepository.existsByTitleAndUserIdAndCollectionIdIsNot(collectionDto.getTitle(), userId, collectionId))
                .thenReturn(true);

        assertThrows(ApplicationException.class, () -> collectionService.editCollection(collectionId, collectionDto));
        verify(collectionRepository, never()).save(any(CollectionEntity.class));
    }

    @Test
    void testDeleteCollection() {
        doNothing().when(collectionRepository).removeAllByCollectionId(collectionId);
        collectionService.deleteCollection(collectionId);
        verify(collectionRepository).removeAllByCollectionId(collectionId);
    }

    @Test
    void testExistsCollection() {
        when(collectionRepository.existsByCollectionId(collectionId)).thenReturn(true);
        assertDoesNotThrow(() -> collectionService.existsCollection(collectionId));
        verify(collectionRepository).existsByCollectionId(collectionId);
    }

    @Test
    void testExistsCollectionNotFound() {
        when(collectionRepository.existsByCollectionId(collectionId)).thenReturn(false);
        assertThrows(ApplicationException.class, () -> collectionService.existsCollection(collectionId));
    }
}
