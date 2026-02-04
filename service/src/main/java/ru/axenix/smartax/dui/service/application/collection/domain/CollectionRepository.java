package ru.axenix.smartax.dui.service.application.collection.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий взаимодействия с БД таблицы <strong>collection</strong>
 *
 * @author Velikanov Artyom.
 */
@Repository
public interface CollectionRepository extends JpaRepository<CollectionEntity, UUID> {

    /**
     * Получение списка актуальных коллекции администратора.
     *
     * @param id идентификатор администратора.
     * @return Список коллекций без контента.
     */
    List<CollectionEntity> findAllByUserIdAndIsCurrentTrue(String id);

    /**
     * Получение списка исторических записей коллекций в порядке убывания даты.
     *
     * @param collectionId Идентификатор коллекции.
     * @return Список истории коллекции без контента.
     */
    List<CollectionEntity> findAllByCollectionIdOrderByCreationDateAsc(UUID collectionId);

    /**
     * Получение контента актуальной коллекции по ее идентификатору.
     *
     * @param collectionId Идентификатор коллекции.
     * @return Контент коллекции.
     */
    Optional<CollectionEntity> findByCollectionIdAndIsCurrentTrue(UUID collectionId);

    /**
     * Удаление всех записей по коллекции.
     *
     * @param collectionId Идентификатор коллекции.
     */
    void removeAllByCollectionId(UUID collectionId);

    /**
     * Проверка наличия нескольких записей по коллекции.
     *
     * @param collectionId Идентификатор коллекции.
     * @return true/false признак наличия исторических записей коллекции.
     */
    boolean existsByCollectionId(UUID collectionId);

    /**
     * Поиск наличия наименования коллекций созданных администратором.
     *
     * @param title Наименование коллекции.
     * @param userId Идентификатор пользователя.
     * @param collectionId Идентификатор коллекции.
     * @return true/false признак наличия наименования коллекции записях коллекции.
     */
    boolean existsByTitleAndUserIdAndCollectionIdIsNot(String title, String userId, UUID collectionId);
}
