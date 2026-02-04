package ru.axenix.smartax.dui.service.application.collection.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axenix.smartax.dui.service.application.collection.domain.CollectionEntity;
import ru.axenix.smartax.dui.service.application.collection.domain.CollectionRepository;
import ru.axenix.smartax.dui.service.application.collection.mapper.CollectionMapper;
import ru.axenix.smartax.dui.service.application.collection.service.CollectionService;
import ru.axenix.smartax.dui.service.error.ErrorDescription;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Сервис управления коллекциями.
 *
 * @author Velikanov Artyom.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;
    private final CollectionMapper collectionMapper;

    /**
     * Получение списка коллекций по идентификатору администратора.
     *
     * <p>
     * Возвращаются только идентификатор и наименование коллекции
     * (контент страниц и конфигурация не загружаются в DTO).
     * </p>
     *
     * @param userId идентификатор администратора.
     * @return список записей коллекций без контента.
     */
    @Override
    public List<CollectionShortDto> getAllCollections(String userId) {
        var entities = collectionRepository.findAllByUserIdAndIsCurrentTrue(userId);
        return collectionMapper.toShortDtoList(entities);
    }

    /**
     * Получение текущей используемой записи коллекции по идентификатору.
     *
     * @param collectionUUID идентификатор коллекции.
     * @return контент коллекции.
     */
    @Override
    public CollectionDto getCollection(UUID collectionUUID) {
        var entity = collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionUUID)
                .orElseThrow(ErrorDescription.COLLECTION_NOT_FOUND::exception);
        return collectionMapper.toDto(entity);
    }

    /**
     * Экспорт файла текущей актуальной коллекции по её идентификатору.
     *
     * @param collectionUUID идентификатор коллекции.
     * @return файл коллекции в формате JSON.
     */
    @Override
    public FileDto exportCollection(UUID collectionUUID) {
        var entity = collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionUUID)
                .orElseThrow(ErrorDescription.COLLECTION_NOT_FOUND::exception);
        var dto = collectionMapper.toDto(entity);

        try {
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(dto);
            return new FileDto(
                    entity.getHistoryId().toString(),
                    new InputStreamResource(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
            );
        } catch (Exception e) {
            log.error("Export collection error for {}", entity.getHistoryId(), e);
            throw ErrorDescription.EXPORT_COLLECTION_ERROR.exception();
        }
    }

    /**
     * Получение истории изменений коллекции.
     *
     * @param collectionUUID идентификатор коллекции.
     * @return список DTO по истории изменений коллекции.
     */
    @Override
    public List<CollectionHistoryDto> getCollectionHistory(UUID collectionUUID) {
        var entities = collectionRepository.findAllByCollectionIdOrderByCreationDateAsc(collectionUUID);
        return collectionMapper.toHistoryDtoList(entities);
    }

    /**
     * Получение конкретной исторической записи коллекции.
     *
     * @param historyUUID идентификатор записи коллекции.
     * @return контент коллекции для выбранной исторической записи.
     */
    @Override
    public CollectionDto getCollectionByHistoryUUID(UUID historyUUID) {
        var entity = collectionRepository.findById(historyUUID)
                .orElseThrow(ErrorDescription.COLLECTION_NOT_FOUND::exception);
        return collectionMapper.toDto(entity);
    }

    /**
     * Создание новой записи коллекции администратором.
     *
     * @param userId     идентификатор администратора.
     * @param collection контент коллекции.
     * @return результат сохранения коллекции.
     */
    @Transactional
    @Override
    public CollectionDto createCollection(String userId, CollectionDto collection) {
        return createCollection(null, userId, collection);
    }

    /**
     * Создание новой записи коллекции администратором.
     *
     * <p>
     * Если {@code collectionUUID} равен {@code null}, то создаётся новая коллекция
     * с новым идентификатором коллекции, совпадающим с {@code historyUUID}.
     * В противном случае создаётся новая запись по уже существующей коллекции.
     * </p>
     *
     * @param collectionUUID идентификатор коллекции (может быть null при создании новой коллекции).
     * @param userId         идентификатор администратора.
     * @param collection     контент коллекции.
     * @return результат создания/редактирования коллекции.
     */
    private CollectionDto createCollection(UUID collectionUUID, String userId, CollectionDto collection) {
        validateCollectionTitle(userId, collection.getTitle(), collectionUUID);

        UUID historyUUID = UUID.randomUUID();

        try {
            CollectionEntity entity = collectionMapper
                    .toEntity(collection, historyUUID, collectionUUID, userId);
            var saved = collectionRepository.save(entity);
            return collectionMapper.toDto(saved);
        } catch (Exception e) {
            log.error("Create collection error", e);
            throw ErrorDescription.EXPORT_COLLECTION_ERROR.exception();
        }
    }

    /**
     * Редактирование коллекции (создание новой записи по коллекции).
     *
     * @param collectionUUID идентификатор коллекции.
     * @param collection     контент коллекции.
     * @return результат редактирования коллекции (новая созданная запись).
     */
    @Transactional
    @Override
    public CollectionDto editCollection(UUID collectionUUID, CollectionDto collection) {
        var entity = collectionRepository.findByCollectionIdAndIsCurrentTrue(collectionUUID)
                .orElseThrow(ErrorDescription.COLLECTION_NOT_FOUND::exception);

        validateCollectionTitle(entity.getUserId(), collection.getTitle(), entity.getCollectionId());

        entity.setIsCurrent(null);
        collectionRepository.save(entity);

        return createCollection(entity.getCollectionId(), entity.getUserId(), collection);
    }

    /**
     * Удаление всех записей по коллекции.
     *
     * @param collectionUUID идентификатор коллекции.
     */
    @Transactional
    @Override
    public void deleteCollection(UUID collectionUUID) {
        collectionRepository.removeAllByCollectionId(collectionUUID);
    }

    /**
     * Валидация наличия коллекции.
     *
     * @param collectionUUID идентификатор коллекции.
     */
    @Override
    public void existsCollection(UUID collectionUUID) {
        ErrorDescription.COLLECTION_NOT_FOUND
                .throwIfFalse(collectionRepository.existsByCollectionId(collectionUUID));
    }

    /**
     * Валидация записи коллекции на уникальность по названию и пользователю.
     *
     * @param userId         идентификатор администратора.
     * @param title          наименование коллекции.
     * @param collectionUUID идентификатор коллекции (может быть null при создании новой коллекции).
     */
    private void validateCollectionTitle(String userId, String title, UUID collectionUUID) {
        ErrorDescription.COLLECTION_TITLE_ALREADY_EXISTS
                .throwIfTrue(collectionRepository.existsByTitleAndUserIdAndCollectionIdIsNot(title, userId, collectionUUID));
    }
}
