package ru.axenix.smartax.dui.service.application.collection.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.axenix.smartax.dui.service.application.collection.domain.CollectionEntity;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionHistoryDto;
import ru.axenix.smartax.dui.service.application.collection.model.CollectionShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Mapper для преобразования между доменной сущностью {@link CollectionEntity}
 * и моделями передачи данных {@link CollectionDto} и {@link CollectionShortDto}.
 *
 * @author Artem Velikanov.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {LocalDateTime.class}
)
public interface CollectionMapper {

    /**
     * Преобразование доменной сущности в полную DTO-модель.
     *
     * @param entity сущность коллекции.
     * @return полная DTO-модель коллекции.
     */
    @Mapping(target = "uuid", source = "collectionId")
    CollectionDto toDto(CollectionEntity entity);

    /**
     * Преобразование списка сущностей в список полных DTO.
     *
     * @param entities список сущностей.
     * @return список DTO-моделей коллекции.
     */
    List<CollectionDto> toDtoList(List<CollectionEntity> entities);

    /**
     * Преобразование сущности в сокращённую DTO-модель.
     * Используется для списков, когда не требуется отдавать pages/mocks/config.
     *
     * @param entity сущность коллекции.
     * @return краткая DTO-модель коллекции.
     */
    @Mapping(target = "uuid", source = "collectionId")
    CollectionShortDto toShortDto(CollectionEntity entity);

    /**
     * Преобразование списка сущностей в список сокращённых DTO.
     *
     * @param entities список сущностей.
     * @return список коротких DTO-моделей.
     */
    List<CollectionShortDto> toShortDtoList(List<CollectionEntity> entities);

    /**
     * Преобразование сущности в DTO-модель исторической записи коллекции.
     *
     * @param entity сущность коллекции.
     * @return DTO исторической записи коллекции.
     */
    @Mapping(target = "uuid", source = "historyId")
    CollectionHistoryDto toHistoryDto(CollectionEntity entity);

    /**
     * Преобразование списка сущностей в список DTO исторических записей коллекции.
     *
     * @param entities список сущностей.
     * @return список DTO исторических записей.
     */
    List<CollectionHistoryDto> toHistoryDtoList(List<CollectionEntity> entities);

    /**
     * Создание новой сущности коллекции из DTO.
     * <p>Технические поля (historyId, creationDate, isCurrent, userId) заполняются в сервисе.</p>
     *
     * @param dto DTO модели коллекции.
     * @return новая сущность коллекции.
     */
    @Mapping(target = "collectionId", source = "uuid")
    @Mapping(target = "historyId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "isCurrent", ignore = true)
    @Mapping(target = "userId", ignore = true)
    CollectionEntity toEntity(CollectionDto dto);

    /**
     * Создание новой сущности коллекции из DTO.
     *
     *
     * @param dto DTO модели коллекции.
     * @param historyUUID идентификатор записи.
     * @param collectionUUID идентификатор коллекции.
     * @param userId идентификатор пользователя.
     * @return новая сущность коллекции.
     */
    @Mapping(target = "collectionId", expression = "java(collectionUUID != null ? collectionUUID : historyUUID)")
    @Mapping(target = "historyId", source = "historyUUID")
    @Mapping(target = "creationDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "isCurrent", constant = "true")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "pages", source = "dto.pages")
    @Mapping(target = "mocks", source = "dto.mocks")
    @Mapping(target = "config", source = "dto.config")
    @Mapping(target = "title", source = "dto.title")
    CollectionEntity toEntity(CollectionDto dto,
                              UUID historyUUID,
                              UUID collectionUUID,
                              String userId);

    /**
     * Частичное обновление сущности коллекции из DTO.
     * <p>
     * Стратегия IGNORE гарантирует, что поля с null в DTO не перезатрут значения в Entity.
     * Используется при PATCH или в сценариях редактирования.
     * </p>
     *
     * @param dto    DTO с частичными обновлениями.
     * @param entity сущность, которую необходимо обновить.
     */
    @Mapping(target = "collectionId", ignore = true)
    @Mapping(target = "historyId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "isCurrent", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateEntityFromDto(CollectionDto dto, @MappingTarget CollectionEntity entity);
}
