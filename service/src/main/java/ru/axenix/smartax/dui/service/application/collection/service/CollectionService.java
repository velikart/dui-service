package ru.axenix.smartax.dui.service.application.collection.service;

import java.util.List;
import java.util.UUID;

import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;
import ru.axenix.smartax.dui.service.application.template.model.FileDto;

/**
 * Интерфейс сервиса управления коллекциями.
 *
 * @author Velikanov Artyom.
 */
public interface CollectionService {

    /**
     * Получение списка коллекций по идентификатору администратора
     *
     * @param userId идентификатор администратора.
     * @return Список записей коллекций без контента.
     */
    List<CollectionShortDto> getAllCollections(String userId);

    /**
     * Получение текущей используемой записи коллекции по идентификатору
     *
     * @param collectionUUID Идентификатор коллекции
     * @return Контент коллекции.
     */
    CollectionDto getCollection(UUID collectionUUID);

    /**
     * Экспорт файла текущей актуальной коллекции по ее идентификатору.
     *
     * @param collectionUUID Идентификатор коллекции.
     * @return файл коллекции.
     */
    FileDto exportCollection(UUID collectionUUID);

    /**
     * Получение истории изменений коллекции.
     *
     * @param collectionUUID идентификатор коллекции.
     * @return Список коллекций по истории изменений.
     */
    List<CollectionHistoryDto> getCollectionHistory(UUID collectionUUID);

    /**
     * Получение конкретной исторической записи коллекции.
     *
     * @param historyUUID идентификатор записи коллекции.
     * @return контент коллекции.
     */
    CollectionDto getCollectionByHistoryUUID(UUID historyUUID);

    /**
     * Создание новой записи коллекции администратором.
     *
     * @param userId идентификатор администратора.
     * @param collection контент коллекции.
     * @return результат сохранения коллекции.
     */
    CollectionDto createCollection(String userId, CollectionDto collection);

    /**
     * Редактирование коллекции (создание новой записи по коллекции).
     *
     * @param collectionUUID идентификатор коллекции.
     * @param collection контент коллекции.
     * @return результат редактирования коллекции (новая созданная запись).
     */
    CollectionDto editCollection(UUID collectionUUID, CollectionDto collection);

    /**
     * Удаление всех записей по коллекции.
     *
     * @param collectionUUID идентификатор коллекции.
     */
    void deleteCollection(UUID collectionUUID);

    /**
     * Валидация наличия коллекции.
     *
     * @param collectionUUID идентификатор коллекции.
     */
    void existsCollection(UUID collectionUUID);
}
