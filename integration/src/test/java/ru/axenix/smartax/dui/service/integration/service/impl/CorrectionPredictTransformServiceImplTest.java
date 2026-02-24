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
import ru.axenix.smartax.dui.service.integration.dto.CorrectionCompositionItem;

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
class CorrectionPredictTransformServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CorrectionPredictTransformServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CorrectionPredictTransformServiceImpl(restTemplate, objectMapper);
        ReflectionTestUtils.setField(service, "seriesServiceUrl", "http://series-service");
        ReflectionTestUtils.setField(service, "correctionsPredictPath", "/api/v1/corrections/predict");
        ReflectionTestUtils.setField(service, "correctionsRecalculatePath", "/api/v1/corrections/recalculate");
    }

    @Test
    void predictAndTransformShouldMapHeaderAndSolutionsAndForwardHeadersAndLiftMeta() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of("Authorization")));
        when(request.getHeaders("Authorization")).thenReturn(toEnumeration(List.of("Bearer token")));

        Map<String, Object> payload = Map.of("series_num", "п.30326");

        Map<String, Object> responseBody = Map.of(
                "prediction_id", "pred_1",
                "meta", Map.of(
                        "default_amount_DP", 1.232,
                        "default_amount_GV", 4.64,
                        "predicted_pastes", List.of("p1", "p2"),
                        "predicted_components", List.of("c1", "c2")
                ),
                "tables", Map.of(
                        "predict_composition", Map.of(
                                "header", List.of(
                                        List.of(Map.of("h", "m1"), Map.of("h", "m2")),
                                        List.of(Map.of("h", "p1"), Map.of("h", "p2"))
                                ),
                                "solutions", List.of(
                                        List.of(
                                                List.of(Map.of("s1", 1), Map.of("s1", 2)),
                                                List.of(Map.of("s1p", 10), Map.of("s1p", 20))
                                        ),
                                        List.of(
                                                List.of(Map.of("s2", 3), Map.of("s2", 4)),
                                                List.of(Map.of("s2p", 30), Map.of("s2p", 40))
                                        )
                                )
                        ),
                        "predict_analysis", List.of(
                                Map.of("a", "h1"),
                                Map.of("a", "h2"),
                                Map.of("a", "h3"),
                                Map.of("a", "a1_1"),
                                Map.of("a", "a1_2"),
                                Map.of("a", "a2_1"),
                                Map.of("a", "a2_2"),
                                Map.of("a", "a3_1"),
                                Map.of("a", "a3_2")
                        )
                )
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/corrections/predict"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(responseBody));

        Map<String, Object> result = service.predictAndTransform(payload, request);

        assertEquals(List.of(Map.of("h", "m1"), Map.of("h", "m2")), result.get("header_mass"));
        assertEquals(List.of(Map.of("h", "p1"), Map.of("h", "p2")), result.get("header_percent"));
        assertEquals(List.of(Map.of("s1", 1), Map.of("s1", 2)), result.get("solution_1_mass"));
        assertEquals(List.of(Map.of("s1p", 10), Map.of("s1p", 20)), result.get("solution_1_percent"));
        assertEquals(List.of(Map.of("s2", 3), Map.of("s2", 4)), result.get("solution_2_mass"));
        assertEquals(List.of(Map.of("s2p", 30), Map.of("s2p", 40)), result.get("solution_2_percent"));
        assertEquals(
                List.of(Map.of("a", "h1"), Map.of("a", "h2"), Map.of("a", "h3")),
                result.get("analysis_header")
        );
        assertEquals(List.of(Map.of("a", "a1_1"), Map.of("a", "a1_2")), result.get("analysis_1"));
        assertEquals(List.of(Map.of("a", "a2_1"), Map.of("a", "a2_2")), result.get("analysis_2"));
        assertEquals(List.of(Map.of("a", "a3_1"), Map.of("a", "a3_2")), result.get("analysis_3"));

        assertEquals("pred_1", result.get("prediction_id"));
        assertEquals(1.232, (Double) result.get("max_dp_percent"), 1e-9);
        assertEquals(4.64, (Double) result.get("max_gv_percent"), 1e-9);
        assertEquals(List.of("p1", "p2"), result.get("dp_selected_items"));
        assertEquals(List.of("c1", "c2"), result.get("gv_selected_items"));

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("http://series-service/api/v1/corrections/predict"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(Object.class)
        );

        HttpEntity capturedEntity = captor.getValue();
        assertEquals("Bearer token", capturedEntity.getHeaders().getFirst("Authorization"));
        assertEquals(payload, capturedEntity.getBody());
    }

    @Test
    void predictAndTransformShouldReturnEmptyArraysAndDefaultsWhenDataMissing() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/corrections/predict"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(Map.of()));

        Map<String, Object> result = service.predictAndTransform(Map.of(), request);

        assertEquals(List.of(), result.get("header_mass"));
        assertEquals(List.of(), result.get("header_percent"));
        assertEquals(List.of(), result.get("analysis_header"));
        assertEquals(List.of(), result.get("analysis_1"));
        assertEquals(List.of(), result.get("analysis_2"));
        assertEquals(List.of(), result.get("analysis_3"));

        assertEquals(null, result.get("prediction_id"));
        assertEquals(null, result.get("max_dp_percent"));
        assertEquals(null, result.get("max_gv_percent"));
        assertEquals(List.of(), result.get("dp_selected_items"));
        assertEquals(List.of(), result.get("gv_selected_items"));
    }

    @Test
    void recalculateAndTransformShouldMapLikePredictAndSendNewFormatUsingMaxPercents() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of("Authorization")));
        when(request.getHeaders("Authorization")).thenReturn(toEnumeration(List.of("Bearer token")));

        Map<String, Object> payload = Map.of(
                "prediction_id", "pred_35a8bf8fe6934f03e66f239cb4e746939d5dbb81809131099676423804a5fd46",
                "max_dp_percent", 3,
                "max_gv_percent", 5,
                "dp_selected_items", List.of("edw", "ed3fsw"),
                "gv_selected_items", List.of("ed3fsw")
        );

        Map<String, Object> responseBody = Map.of(
                "tables", Map.of(
                        "predict_composition", Map.of(
                                "header", List.of(
                                        List.of(Map.of("h", "m1")),
                                        List.of(Map.of("h", "p1"))
                                ),
                                "solutions", List.of(
                                        List.of(
                                                List.of(Map.of("s1", 1)),
                                                List.of(Map.of("s1p", 10))
                                        )
                                )
                        ),
                        "predict_analysis", List.of(
                                Map.of("a", "h1"),
                                Map.of("a", "h2"),
                                Map.of("a", "h3"),
                                Map.of("a", "a1_1"),
                                Map.of("a", "a1_2"),
                                Map.of("a", "a2_1"),
                                Map.of("a", "a2_2"),
                                Map.of("a", "a3_1"),
                                Map.of("a", "a3_2")
                        )
                )
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/corrections/recalculate"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(responseBody));

        Map<String, Object> result = service.recalculateAndTransform(payload, request);

        assertEquals(List.of(Map.of("h", "m1")), result.get("header_mass"));
        assertEquals(List.of(Map.of("h", "p1")), result.get("header_percent"));
        assertEquals(List.of(Map.of("s1", 1)), result.get("solution_1_mass"));
        assertEquals(List.of(Map.of("s1p", 10)), result.get("solution_1_percent"));
        assertEquals(
                List.of(Map.of("a", "h1"), Map.of("a", "h2"), Map.of("a", "h3")),
                result.get("analysis_header")
        );

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("http://series-service/api/v1/corrections/recalculate"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(Object.class)
        );

        HttpEntity capturedEntity = captor.getValue();
        assertEquals("Bearer token", capturedEntity.getHeaders().getFirst("Authorization"));

        Map<String, Object> expectedPayload = Map.of(
                "prediction_id", "pred_35a8bf8fe6934f03e66f239cb4e746939d5dbb81809131099676423804a5fd46",
                "user_changes", Map.of(
                        "user_selections", Map.of(
                                "selected_dp", List.of("edw", "ed3fsw"),
                                "selected_gv", List.of("ed3fsw"),
                                "paste_amounts", 3,
                                "component_amounts", 5
                        )
                )
        );

        assertEquals(expectedPayload, capturedEntity.getBody());
    }

    @Test
    void getFinalCompositionDpShouldMapDroplistDpToIdAndName() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> payload = Map.of("series_num", "п.30326");

        Map<String, Object> responseBody = Map.of(
                "meta", Map.of(
                        "droplist_DP", List.of(
                                "PPG-04-0008 (MG600734) зеленая",
                                "PPW-04-0070 (BLR 895) белая"
                        )
                )
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/corrections/predict"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(responseBody));

        List<CorrectionCompositionItem> result = service.getFinalCompositionDp(payload, request);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item ->
                "PPG-04-0008 (MG600734) зеленая".equals(item.getId())
                        && "PPG-04-0008 (MG600734) зеленая".equals(item.getName())
        ));
        assertTrue(result.stream().anyMatch(item ->
                "PPW-04-0070 (BLR 895) белая".equals(item.getId())
                        && "PPW-04-0070 (BLR 895) белая".equals(item.getName())
        ));
    }

    @Test
    void getFinalCompositionGvShouldMapDroplistGvToIdAndName() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(toEnumeration(List.of()));

        Map<String, Object> payload = Map.of("series_num", "п.30326");

        Map<String, Object> responseBody = Map.of(
                "meta", Map.of(
                        "droplist_GV", List.of(
                                "UNIQ-CLAD 1047",
                                "PTAF-3456"
                        )
                )
        );

        when(restTemplate.exchange(
                eq("http://series-service/api/v1/corrections/predict"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok(responseBody));

        List<CorrectionCompositionItem> result = service.getFinalCompositionGv(payload, request);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item ->
                "UNIQ-CLAD 1047".equals(item.getId())
                        && "UNIQ-CLAD 1047".equals(item.getName())
        ));
        assertTrue(result.stream().anyMatch(item ->
                "PTAF-3456".equals(item.getId())
                        && "PTAF-3456".equals(item.getName())
        ));
    }

    private static <T> Enumeration<T> toEnumeration(List<T> list) {
        return java.util.Collections.enumeration(list);
    }
}