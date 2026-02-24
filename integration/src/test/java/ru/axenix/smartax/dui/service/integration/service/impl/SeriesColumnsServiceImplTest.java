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
import ru.axenix.smartax.dui.service.integration.dto.SeriesColumnsResponse;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeriesColumnsServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SeriesColumnsServiceImpl seriesColumnsService;

    @BeforeEach
    void setUp() {
        seriesColumnsService = new SeriesColumnsServiceImpl(restTemplate, objectMapper);
        ReflectionTestUtils.setField(seriesColumnsService, "seriesServiceUrl", "http://series-service");
        ReflectionTestUtils.setField(seriesColumnsService, "seriesDataPath", "/api/v1/series/{seriesNum}");
    }

    @Test
    void getMainColumnsShouldReturnOnlyDynamicAndForwardHeaders() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of("Authorization", "X-Correlation-Id")));
        when(request.getHeaders("Authorization")).thenReturn(toEnumeration(List.of("Bearer token")));
        when(request.getHeaders("X-Correlation-Id")).thenReturn(toEnumeration(List.of("corr-id")));

        Map<String, Object> body = Map.of(
                "dynamic_cols", List.of("Проба", "Анализная карточка", "Общая масса"),
                "static_cols", List.of("field1", "field2", "field3"),
                "payload", Map.of(
                        "extra", "value"
                )
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/п.37427"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getMainColumns("п.37427", request);

        assertEquals(3, result.getColumns().size());
        assertEquals(List.of("Анализная карточка", "Общая масса", "Проба"),
                result.getColumns().stream().map(c -> c.getName()).toList());
        assertEquals(List.of("Анализная карточка", "Общая масса", "Проба"),
                result.getColumns().stream().map(c -> c.getTitle()).toList());
        assertTrue(result.getColumns().stream().allMatch(column -> "string".equals(column.getType())));
        assertTrue(result.getColumns().stream().allMatch(column -> "30px".equals(column.getWidth())));
        assertTrue(result.getColumns().stream()
                .filter(column -> List.of("Проба", "Анализная карточка", "Условная вязкость", "Блеск при 60 град")
                        .contains(column.getName()))
                .allMatch(column -> Boolean.FALSE.equals(column.getIsVerticalOrientation())));
        assertTrue(result.getColumns().stream()
                .filter(column -> !List.of("Проба", "Анализная карточка", "Условная вязкость", "Блеск при 60 град")
                        .contains(column.getName()))
                .allMatch(column -> Boolean.TRUE.equals(column.getIsVerticalOrientation())));

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("http://series-service/api/v1/series/п.37427"),
                eq(HttpMethod.GET),
                captor.capture(),
                eq(Object.class)
        );
        HttpEntity capturedEntity = captor.getValue();
        assertEquals("Bearer token", capturedEntity.getHeaders().getFirst("Authorization"));
        assertEquals("corr-id", capturedEntity.getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void getMainColumnsShouldReturnOnlyDynamicInNestedStructure() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "data", Map.of(
                        "rows", List.of(
                                Map.of("id", 1),
                                Map.of(
                                        "meta", Map.of(
                                                "dynamic_cols", List.of("d1", "d2"),
                                                "static_cols", List.of("s1", "s2")
                                        )
                                )
                        )
                )
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/100"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getMainColumns("100", request);

        assertEquals(List.of("d1", "d2"), result.getColumns().stream().map(c -> c.getName()).toList());
        assertEquals(List.of("d1", "d2"), result.getColumns().stream().map(c -> c.getTitle()).toList());
    }

    @Test
    void getMainColumnsShouldReturnDynamicColumnsSortedAlphabetically() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "dynamic_cols", List.of("d1", "d2", "d3"),
                "static_cols", List.of("s1")
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/flat"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getMainColumns("flat", request);

        assertEquals(3, result.getColumns().size());
        assertEquals("d1", result.getColumns().get(0).getName());
        assertEquals("d1", result.getColumns().get(0).getTitle());
        assertEquals("d2", result.getColumns().get(1).getName());
        assertEquals("d2", result.getColumns().get(1).getTitle());
        assertEquals("d3", result.getColumns().get(2).getName());
        assertEquals("d3", result.getColumns().get(2).getTitle());
    }

    @Test
    void getColumnsShouldSupportObjectItemsInCols() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "dynamic_cols", List.of(Map.of("title", "Title A"), Map.of("name", "Title B")),
                "static_cols", List.of(Map.of("name", "fieldA"), Map.of("title", "fieldB"))
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/obj"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getMainColumns("obj", request);

        assertEquals(List.of("Title A", "Title B"), result.getColumns().stream().map(c -> c.getName()).toList());
        assertEquals(List.of("Title A", "Title B"), result.getColumns().stream().map(c -> c.getTitle()).toList());
    }

    @Test
    void getColumnsShouldReturnDynamicWhenStaticMissing() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "dynamic_cols", List.of("d1", "d2")
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/dyn-only"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getMainColumns("dyn-only", request);

        assertEquals(List.of("d1", "d2"), result.getColumns().stream().map(c -> c.getName()).toList());
        assertEquals(List.of("d1", "d2"), result.getColumns().stream().map(c -> c.getTitle()).toList());
    }

    @Test
    void getAdditionalColumnsShouldReturnStaticColumnsAfterFirstThree() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "dynamic_cols", List.of("d1", "d2"),
                "static_cols", List.of("s1", "s2", "s3", "s4", "s5")
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/additional"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getAdditionalColumns("additional", request);

        assertEquals(List.of("s4", "s5"), result.getColumns().stream().map(c -> c.getName()).toList());
        assertEquals(List.of("s4", "s5"), result.getColumns().stream().map(c -> c.getTitle()).toList());
    }

    @Test
    void getAdditionalColumnsShouldReturnEmptyWhenStaticHasThreeOrLess() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> body = Map.of(
                "static_cols", List.of("s1", "s2", "s3")
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/additional-empty"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(body));

        SeriesColumnsResponse result = seriesColumnsService.getAdditionalColumns("additional-empty", request);

        assertEquals(0, result.getColumns().size());
    }

    @Test
    void getColumnsShouldReturnEmptyWhenCompositionMissing() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/series/unknown"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(Map.of()));

        SeriesColumnsResponse result = seriesColumnsService.getMainColumns("unknown", request);

        assertEquals(0, result.getColumns().size());
    }

    @Test
    void getColumnsShouldUseConfiguredPath() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        ReflectionTestUtils.setField(seriesColumnsService, "seriesDataPath", "/custom/series/{seriesNum}");

        when(restTemplate.exchange(
                eq("http://series-service/custom/series/42"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(Map.of()));

        seriesColumnsService.getMainColumns("42", request);

        verify(restTemplate).exchange(
                eq("http://series-service/custom/series/42"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    private static <T> Enumeration<T> toEnumeration(List<T> list) {
        return java.util.Collections.enumeration(list);
    }
}
