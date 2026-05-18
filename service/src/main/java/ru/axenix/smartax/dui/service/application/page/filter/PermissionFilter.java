package ru.axenix.smartax.dui.service.application.page.filter;

/**
 * Контракт для фильтрации произвольной JSON-подобной структуры
 * (Map/List/примитивы) по правам текущего пользователя.
 */
public interface PermissionFilter {

    /**
     * Фильтрует входной объект по правам текущего пользователя.
     *
     * @param value JSON-подобная структура (List/Map/примитив)
     * @return отфильтрованная структура, где недоступные узлы удалены
     */
    Object filter(Object value);
}
