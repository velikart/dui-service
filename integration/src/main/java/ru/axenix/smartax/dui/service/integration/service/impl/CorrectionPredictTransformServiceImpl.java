package ru.axenix.smartax.dui.service.integration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.axenix.smartax.dui.service.integration.dto.CorrectionCompositionItem;
import ru.axenix.smartax.dui.service.integration.service.CorrectionPredictTransformService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CorrectionPredictTransformServiceImpl
        implements CorrectionPredictTransformService {

    private static final String TABLES_FIELD = "tables";
    private static final String PREDICT_COMPOSITION_FIELD = "predict_composition";
    private static final String HEADER_FIELD = "header";
    private static final String SOLUTIONS_FIELD = "solutions";
    private static final String PREDICT_ANALYSIS_FIELD = "predict_analysis";

    private static final String META_FIELD = "meta";
    private static final String DROPLIST_DP_FIELD = "droplist_DP";
    private static final String DROPLIST_GV_FIELD = "droplist_GV";

    private static final String DEFAULT_AMOUNT_DP_FIELD = "default_amount_DP";
    private static final String DEFAULT_AMOUNT_GV_FIELD = "default_amount_GV";
    private static final String PREDICTED_PASTES_FIELD = "predicted_pastes";
    private static final String PREDICTED_COMPONENTS_FIELD = "predicted_components";

    private static final String HEADER_MASS_KEY = "header_mass";
    private static final String HEADER_PERCENT_KEY = "header_percent";
    private static final String ANALYSIS_HEADER_KEY = "analysis_header";
    private static final String ANALYSIS_1_KEY = "analysis_1";
    private static final String ANALYSIS_2_KEY = "analysis_2";
    private static final String ANALYSIS_3_KEY = "analysis_3";

    private static final String PREDICT_PATH = "predict";
    private static final String RECALCULATE_PATH = "recalculate";

    private static final String PREDICTION_ID_FIELD = "prediction_id";
    private static final String USER_CHANGES_FIELD = "user_changes";
    private static final String USER_SELECTIONS_FIELD = "user_selections";

    private static final String DP_SELECTED_ITEMS_FIELD = "dp_selected_items";
    private static final String GV_SELECTED_ITEMS_FIELD = "gv_selected_items";

    private static final String SELECTED_DP_FIELD = "selected_dp";
    private static final String SELECTED_GV_FIELD = "selected_gv";
    private static final String PASTE_AMOUNTS_FIELD = "paste_amounts";
    private static final String COMPONENT_AMOUNTS_FIELD = "component_amounts";

    private static final String MAX_DP_PERCENT_FIELD = "max_dp_percent";
    private static final String MAX_GV_PERCENT_FIELD = "max_gv_percent";

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "content-length",
            "transfer-encoding",
            "host",
            "connection",
            "accept-encoding"
    );

    @Value("${client.series-service.url}")
    private String seriesServiceUrl;

    @Value("${client.series-service.corrections-predict-path:/api/v1/corrections/predict}")
    private String correctionsPredictPath;

    @Value("${client.series-service.corrections-recalculate-path:/api/v1/corrections/recalculate}")
    private String correctionsRecalculatePath;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CorrectionPredictTransformServiceImpl(
            @Qualifier("seriesServiceRestTemplate") RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> predictAndTransform(Object payload, HttpServletRequest request) {
        JsonNode body = fetchCorrectionsBody(payload, request, PREDICT_PATH);

        Map<String, Object> result = transform(body);

        result.put(PREDICTION_ID_FIELD, asTextOrNull(body.path(PREDICTION_ID_FIELD)));

        JsonNode metaNode = body.path(META_FIELD);

        result.put(MAX_DP_PERCENT_FIELD, toDoubleOrNull(metaNode.path(DEFAULT_AMOUNT_DP_FIELD)));
        result.put(MAX_GV_PERCENT_FIELD, toDoubleOrNull(metaNode.path(DEFAULT_AMOUNT_GV_FIELD)));

        result.put(DP_SELECTED_ITEMS_FIELD, extractTextArray(metaNode.path(PREDICTED_PASTES_FIELD)));
        result.put(GV_SELECTED_ITEMS_FIELD, extractTextArray(metaNode.path(PREDICTED_COMPONENTS_FIELD)));

        return result;
    }

    @Override
    public Map<String, Object> recalculateAndTransform(Object payload, HttpServletRequest request) {
        JsonNode body = fetchCorrectionsBody(payload, request, RECALCULATE_PATH);
        return transform(body);
    }

    @Override
    public List<CorrectionCompositionItem> getFinalCompositionDp(Object payload, HttpServletRequest request) {
        JsonNode body = fetchCorrectionsBody(payload, request, PREDICT_PATH);
        return mapDroplist(body.path(META_FIELD).path(DROPLIST_DP_FIELD));
    }

    @Override
    public List<CorrectionCompositionItem> getFinalCompositionGv(Object payload, HttpServletRequest request) {
        JsonNode body = fetchCorrectionsBody(payload, request, PREDICT_PATH);
        return mapDroplist(body.path(META_FIELD).path(DROPLIST_GV_FIELD));
    }

    private List<CorrectionCompositionItem> mapDroplist(JsonNode droplistNode) {
        if (!droplistNode.isArray()) {
            return List.of();
        }

        List<CorrectionCompositionItem> result = new ArrayList<>();

        for (JsonNode itemNode : droplistNode) {
            String value = itemNode != null && !itemNode.isNull()
                    ? itemNode.asText(null)
                    : null;

            if (value != null && !value.isBlank()) {
                result.add(new CorrectionCompositionItem(value, value));
            }
        }

        result.sort(Comparator.comparing(
                CorrectionCompositionItem::getId,
                String.CASE_INSENSITIVE_ORDER
        ));

        return result;
    }

    private JsonNode fetchCorrectionsBody(Object payload, HttpServletRequest request, String type) {
        HttpHeaders headers = extractHeaders(request);
        String path = resolveCorrectionsPath(type);

        String url = UriComponentsBuilder.fromUriString(seriesServiceUrl)
                .path(path)
                .build()
                .toUriString();

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(preparePayload(payload, type), headers),
                Object.class
        );

        return objectMapper.valueToTree(response.getBody());
    }

    private Object preparePayload(Object payload, String type) {
        if (!RECALCULATE_PATH.equals(type)) {
            return payload;
        }

        Map<String, Object> source = asMap(payload);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put(PREDICTION_ID_FIELD, source.get(PREDICTION_ID_FIELD));

        Map<String, Object> userSelections = new LinkedHashMap<>();
        userSelections.put(SELECTED_DP_FIELD, asListOfStrings(source.get(DP_SELECTED_ITEMS_FIELD)));
        userSelections.put(SELECTED_GV_FIELD, asListOfStrings(source.get(GV_SELECTED_ITEMS_FIELD)));

        userSelections.put(PASTE_AMOUNTS_FIELD, toInteger(source.get(MAX_DP_PERCENT_FIELD)));
        userSelections.put(COMPONENT_AMOUNTS_FIELD, toInteger(source.get(MAX_GV_PERCENT_FIELD)));

        Map<String, Object> userChanges = new LinkedHashMap<>();
        userChanges.put(USER_SELECTIONS_FIELD, userSelections);

        result.put(USER_CHANGES_FIELD, userChanges);

        return result;
    }

    private Map<String, Object> asMap(Object payload) {
        if (payload instanceof Map<?, ?> mapValue) {
            return objectMapper.convertValue(mapValue, Map.class);
        }
        return Collections.emptyMap();
    }

    private List<String> asListOfStrings(Object value) {
        if (value instanceof List<?> listValue) {
            return listValue.stream()
                    .map(String::valueOf)
                    .toList();
        }
        if (value == null) {
            return List.of();
        }
        return List.of(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        Integer result = null;

        if (value instanceof Number number) {
            result = number.intValue();
        } else {
            try {
                if (value != null) {
                    result = Integer.parseInt(String.valueOf(value));
                }
            } catch (NumberFormatException ignored) {
                // nothing
            }
        }

        return result;
    }

    private String resolveCorrectionsPath(String type) {
        return RECALCULATE_PATH.equals(type)
                ? correctionsRecalculatePath
                : correctionsPredictPath;
    }

    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (request != null) {
            Enumeration<String> headerNames = request.getHeaderNames();

            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    if (!isHopByHopHeader(headerName)) {
                        headers.put(headerName, Collections.list(request.getHeaders(headerName)));
                    }
                }
            }
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private boolean isHopByHopHeader(String headerName) {
        String name = headerName == null ? "" : headerName.toLowerCase();
        return HOP_BY_HOP_HEADERS.contains(name);
    }

    private Map<String, Object> transform(JsonNode responseBody) {
        Map<String, Object> result = new LinkedHashMap<>();

        JsonNode compositionNode = responseBody.path(TABLES_FIELD)
                .path(PREDICT_COMPOSITION_FIELD);

        JsonNode headerNode = compositionNode.path(HEADER_FIELD);
        JsonNode analysisNode = responseBody.path(TABLES_FIELD)
                .path(PREDICT_ANALYSIS_FIELD);

        result.put(HEADER_MASS_KEY, extractArrayAt(headerNode, 0));
        result.put(HEADER_PERCENT_KEY, extractArrayAt(headerNode, 1));
        result.put(ANALYSIS_HEADER_KEY, extractRange(analysisNode, 0, 3));
        result.put(ANALYSIS_1_KEY, extractRange(analysisNode, 3, 2));
        result.put(ANALYSIS_2_KEY, extractRange(analysisNode, 5, 2));
        result.put(ANALYSIS_3_KEY, extractRange(analysisNode, 7, 2));

        JsonNode solutionsNode = compositionNode.path(SOLUTIONS_FIELD);
        if (solutionsNode.isArray()) {
            for (int i = 0; i < solutionsNode.size(); i++) {
                JsonNode solutionNode = solutionsNode.get(i);
                String prefix = "solution_" + (i + 1);

                result.put(prefix + "_mass", extractArrayAt(solutionNode, 0));
                result.put(prefix + "_percent", extractArrayAt(solutionNode, 1));
            }
        }

        return result;
    }

    private Object extractArrayAt(JsonNode node, int index) {
        Object result = Collections.emptyList();

        if (node.isArray() && index < node.size()) {
            JsonNode value = node.get(index);
            if (value.isArray()) {
                result = objectMapper.convertValue(value, Object.class);
            }
        }

        return result;
    }

    private List<Object> extractRange(JsonNode node, int start, int length) {
        List<Object> result = List.of();

        if (node.isArray() && node.size() > start && length > 0) {
            int end = Math.min(node.size(), start + length);
            List<Object> temp = new ArrayList<>();

            for (int i = start; i < end; i++) {
                temp.add(objectMapper.convertValue(node.get(i), Object.class));
            }

            result = temp;
        }

        return result;
    }

    private String asTextOrNull(JsonNode node) {
        String result = null;

        if (node != null && !node.isNull() && !node.isMissingNode()) {
            String text = node.asText(null);
            if (text != null && !text.isBlank()) {
                result = text;
            }
        }

        return result;
    }

    private Double toDoubleOrNull(JsonNode node) {
        Double result = null;

        if (node != null && !node.isNull() && !node.isMissingNode()) {
            if (node.isNumber()) {
                result = node.doubleValue();
            } else {
                try {
                    String txt = node.asText(null);
                    if (txt != null) {
                        result = Double.parseDouble(txt);
                    }
                } catch (NumberFormatException ignored) {
                    // nothing
                }
            }
        }

        return result;
    }

    private List<String> extractTextArray(JsonNode node) {
        List<String> result = List.of();

        if (node != null && node.isArray()) {
            List<String> temp = new ArrayList<>();
            for (JsonNode item : node) {
                String v = item == null || item.isNull()
                        ? null
                        : item.asText(null);

                if (v != null && !v.isBlank()) {
                    temp.add(v);
                }
            }
            result = temp;
        }

        return result;
    }
}