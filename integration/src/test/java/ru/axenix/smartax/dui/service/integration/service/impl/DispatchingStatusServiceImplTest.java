package ru.axenix.smartax.dui.service.integration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ru.axenix.smartax.dui.service.integration.dto.DispatchingStatusCountsResponse;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchingStatusServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DispatchingStatusServiceImpl dispatchingStatusService;

    @BeforeEach
    void setUp() {
        dispatchingStatusService = new DispatchingStatusServiceImpl(restTemplate, objectMapper);
        ReflectionTestUtils.setField(dispatchingStatusService, "seriesServiceUrl",
                "http://series-service");
        ReflectionTestUtils.setField(dispatchingStatusService, "dispatchingTablePath",
                "/api/v1/dispatching/table");
    }

    @Test
    void getStatusCountsShouldReturnItemsAndTotalAndForwardHeadersAndQuery() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getQueryString()).thenReturn("limit=100&offset=0");
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of("Authorization", "X-Correlation-Id")));
        when(request.getHeaders("Authorization")).thenReturn(toEnumeration(List.of("Bearer token")));
        when(request.getHeaders("X-Correlation-Id")).thenReturn(toEnumeration(List.of("corr-id")));

        Map<String, Object> body = Map.of(
                "items", List.of(
                        Map.of("status", "NEW"),
                        Map.of("status", "IN_PROGRESS"),
                        Map.of("status", "NEW"),
                        Map.of("status", "DONE"),
                        Map.of("status", "IN_PROGRESS")
                ),
                "total", 5,
                "limit", 100,
                "offset", 0
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/dispatching/table?limit=100&offset=0"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        DispatchingStatusCountsResponse result = dispatchingStatusService.getStatusCounts(request);

        assertEquals(List.of("2", "2", "1"), result.getItems());
        assertEquals("5", result.getTotal());

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("http://series-service/api/v1/dispatching/table?limit=100&offset=0"),
                eq(HttpMethod.GET),
                captor.capture(),
                eq(Object.class)
        );
        HttpEntity capturedEntity = captor.getValue();
        assertEquals("Bearer token", capturedEntity.getHeaders().getFirst("Authorization"));
        assertEquals("corr-id", capturedEntity.getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void getStatusCountsShouldSupportObjectStatusRepresentation() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "items", List.of(
                        Map.of("status", Map.of("value", "S1")),
                        Map.of("status", Map.of("name", "S2")),
                        Map.of("status", Map.of("title", "S2")),
                        Map.of("status", "S1")
                ),
                "total", 4,
                "limit", 100,
                "offset", 0
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/dispatching/table"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        DispatchingStatusCountsResponse result = dispatchingStatusService.getStatusCounts(request);

        assertEquals(List.of("2", "2"), result.getItems());
        assertEquals("4", result.getTotal());
    }


    @Test
    void getTableWithRecordsShouldMapPayloadToDispatchingQueryParameters() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of("Authorization")));
        when(request.getHeaders("Authorization")).thenReturn(toEnumeration(List.of("Bearer token")));

        Map<String, Object> payload = Map.of(
                "filter", List.of(
                        Map.of("name", "series_num", "condition", "LIKE", "value", "32243",
                                "type", "string"),
                        Map.of("name", "productionStageStatusNm", "condition", "EQUAL", "value",
                                List.of("В работе", "Завершены"), "type", "select")
                ),
                "sort", List.of(
                        Map.of("name", "production_stage_status_nm", "direction", "DESC")
                ),
                "pager", Map.of("page", 0, "limit", 10)
        );

        Map<String, Object> responseBody = Map.of("items", List.of(Map.of("id", 1)), "total", 0);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(responseBody));

        Map<String, Object> result = dispatchingStatusService.getTableWithRecords(payload, request);

        assertEquals(List.of(Map.of("id", 1)), result.get("records"));
        assertEquals(0, result.get("total"));
        assertTrue(!result.containsKey("items"));

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(Object.class)
        );

        String url = urlCaptor.getValue();
        assertTrue(url.startsWith("http://series-service/api/v1/dispatching/table?"));
        assertTrue(url.contains("limit=10"));
        assertTrue(url.contains("offset=0"));
        assertTrue(url.contains("search=32243"));
        assertTrue(url.contains("status="));
        assertTrue(url.contains("sort_by=production_stage_status_nm"));
        assertTrue(url.contains("sort_dir=DESC"));

        HttpEntity capturedEntity = entityCaptor.getValue();
        assertEquals("Bearer token", capturedEntity.getHeaders().getFirst("Authorization"));
        assertNull(capturedEntity.getBody());
    }

    private static <T> Enumeration<T> toEnumeration(List<T> list) {
        return java.util.Collections.enumeration(list);
    }
}
