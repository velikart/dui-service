package ru.axenix.smartax.dui.service.application.page.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.axenix.smartax.auth.common.service.SecurityService;
import ru.axenix.smartax.common.security.SecurityContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация фильтра по правам доступа на Action.
 * <p>
 * Если узел содержит поле {@code permissions} с массивом строк, узел остаётся
 * в результате только при наличии всех указанных прав у текущего пользователя.
 * Роли пользователя извлекаются из {@link SecurityContext}.
 */
@Component
@RequiredArgsConstructor
public class ActionPermissionFilter implements PermissionFilter {

    private static final Object SKIP_NODE = new Object();

    private final SecurityService securityService;

    /**
     * Рекурсивно фильтрует JSON-подобную структуру.
     *
     * @param value исходный объект (Map/List/примитив)
     * @return отфильтрованный объект того же типа
     */
    @Override
    public Object filter(Object value) {
        Object filteredValue = filterValue(value);
        return filteredValue == SKIP_NODE ? Collections.emptyMap() : filteredValue;
    }

    /**
     * Фильтрует узел-объект и его дочерние элементы.
     *
     * @param node узел структуры
     * @return {@code SKIP_NODE}, если доступ запрещён, иначе отфильтрованный узел
     */
    private Object filterNode(Map<String, Object> node) {
        if (!hasPermission(node)) {
            return SKIP_NODE;
        }

        Map<String, Object> filtered = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : node.entrySet()) {
            Object filteredValue = filterValue(entry.getValue());
            if (filteredValue != SKIP_NODE) {
                filtered.put(entry.getKey(), filteredValue);
            }
        }
        return filtered;
    }

    private Object filterValue(Object value) {
        if (value instanceof Map<?, ?> mapValue) {
            @SuppressWarnings("unchecked")
            Map<String, Object> typedMap = (Map<String, Object>) mapValue;
            return filterNode(typedMap);
        }
        if (value instanceof List<?> listValue) {
            List<Object> filteredList = new ArrayList<>();
            for (Object item : listValue) {
                Object filteredItem = filterValue(item);
                if (filteredItem != SKIP_NODE) {
                    filteredList.add(filteredItem);
                }
            }
            return filteredList;
        }
        return value;
    }

    /**
     * Проверяет доступ к узлу на основании поля {@code permissions}.
     *
     * @param node узел структуры
     * @return {@code true}, если узел доступен, иначе {@code false}
     */
    private boolean hasPermission(Map<String, Object> node) {
        Object permissionValue = node.get("permissions");
        if (!(permissionValue instanceof Collection<?> permissionsCollection)) {
            return true;
        }

        Set<String> roles = SecurityContext.getUserInfoOrEmpty().getRoles();
        Set<String> permissions = permissionsCollection.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .collect(java.util.stream.Collectors.toSet());
        return isAuthorized(permissions, roles);
    }

    /**
     * Проверяет, что текущий пользователь авторизован по всем указанным permissions.
     *
     * @param permissions набор требуемых permissions
     * @param roles роли текущего пользователя
     * @return {@code true}, если все permissions разрешены
     */
    private boolean isAuthorized(Set<String> permissions, Set<String> roles) {
        return permissions.stream().allMatch(permission -> securityService.isAuthorized(permission, roles));
    }
}