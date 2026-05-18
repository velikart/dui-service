package ru.axenix.smartax.dui.service.application.page.filter;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.axenix.smartax.auth.common.service.SecurityService;
import ru.axenix.smartax.common.security.SecurityContext;
import ru.axenix.smartax.common.security.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ActionPermissionFilterTest {

    private final SecurityService securityService = mock(SecurityService.class);
    private final ActionPermissionFilter filter = new ActionPermissionFilter(securityService);

    @Test
    void filter_RemovesUnauthorizedTopLevelNode() {
        List<Map<String, Object>> source = List.of(
            Map.of("key", "public"),
            Map.of("permissions", List.of("SECURED"), "key", "secret")
        );
        when(securityService.isAuthorized("SECURED", Set.of("ROLE_USER"))).thenReturn(false);
        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));

        Object result;
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            result = filter.filter(source);
        }

        assertNotNull(result);
        assertEquals(List.of(Map.of("key", "public")), result);
    }

    @Test
    void filter_RemovesUnauthorizedNestedNode() {
        List<Map<String, Object>> source = List.of(
            Map.of(
                "key", "root",
                "children", List.of(
                    Map.of("key", "allowed"),
                    Map.of("permissions", List.of("SECURED"), "key", "secret")
                )
            )
        );
        when(securityService.isAuthorized("SECURED", Set.of("ROLE_USER"))).thenReturn(false);
        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result;
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            result = (List<Map<String, Object>>) filter.filter(source);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> children = (List<Map<String, Object>>) result.get(0).get("children");
        assertEquals(1, children.size());
        assertEquals("allowed", children.get(0).get("key"));
    }

    @Test
    void filter_RemovesUnauthorizedObjectField() {
        Map<String, Object> source = Map.of(
            "type", "container",
            "securedBlock", Map.of(
                "permissions", List.of("SECURED"),
                "value", "secret"
            ),
            "publicBlock", Map.of("value", "visible")
        );
        when(securityService.isAuthorized("SECURED", Set.of("ROLE_USER"))).thenReturn(false);
        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));

        @SuppressWarnings("unchecked")
        Map<String, Object> result;
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            result = (Map<String, Object>) filter.filter(source);
        }

        assertEquals("container", result.get("type"));
        assertEquals(false, result.containsKey("securedBlock"));
        assertEquals(true, result.containsKey("publicBlock"));
    }

    @Test
    void filter_RemovesUnauthorizedArrayItem() {
        List<Map<String, Object>> source = List.of(
            Map.of("id", "1"),
            Map.of("permissions", List.of("SECURED"), "id", "2")
        );
        when(securityService.isAuthorized("SECURED", Set.of("ROLE_USER"))).thenReturn(false);
        UserInfo userInfo = new UserInfo();
        userInfo.setRoles(Set.of("ROLE_USER"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result;
        try (MockedStatic<SecurityContext> mockedContext = mockStatic(SecurityContext.class)) {
            mockedContext.when(SecurityContext::getUserInfoOrEmpty).thenReturn(userInfo);
            result = (List<Map<String, Object>>) filter.filter(source);
        }

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).get("id"));
    }
}