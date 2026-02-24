package ru.axenix.smartax.dui.service.integration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.axenix.smartax.dui.service.integration.dto.SeriesColumn;
import ru.axenix.smartax.dui.service.integration.dto.SeriesColumnsResponse;
import ru.axenix.smartax.dui.service.integration.service.SeriesColumnsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

@Service
public class SeriesColumnsServiceImpl implements SeriesColumnsService {

    private static final String COLUMN_TYPE_STRING = "string";
    private static final String NAME_FIELD = "name";
    private static final String TITLE_FIELD = "title";
    private static final String DYNAMIC_COLS_FIELD = "dynamic_cols";
    private static final String STATIC_COLS_FIELD = "static_cols";
    private static final Boolean IS_READONLY = false;
    private static final String MAIN_COLUMN_WIDTH = "30px";
    private static final int FIXED_STATIC_COLUMNS_COUNT = 3;
    private static final List<String> NON_VERTICAL_COLUMNS = List.of(
            "Проба",
            "Анализная карточка",
            "Условная вязкость",
            "Блеск при 60 град"
    );

    @Value("${client.series-service.url}")
    private String seriesServiceUrl;

    @Value("${client.series-service.series-path:/api/v1/series/{seriesNum}}")
    private String seriesDataPath;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SeriesColumnsServiceImpl(
            @Qualifier("seriesServiceRestTemplate") RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public SeriesColumnsResponse getColumns(String seriesNum, HttpServletRequest request) {
        return getMainColumns(seriesNum, request);
    }

    @Override
    public SeriesColumnsResponse getMainColumns(String seriesNum, HttpServletRequest request) {
        JsonNode body = fetchSeriesBody(seriesNum, request);
        JsonNode dynamicColumns = findField(body, DYNAMIC_COLS_FIELD);

        List<SeriesColumn> columns = new ArrayList<>();
        appendColumns(columns, dynamicColumns, 0, Integer.MAX_VALUE);
        columns.sort(Comparator.comparing(SeriesColumn::getName, String.CASE_INSENSITIVE_ORDER));

        return new SeriesColumnsResponse(columns);
    }

    @Override
    public SeriesColumnsResponse getAdditionalColumns(String seriesNum, HttpServletRequest request) {
        JsonNode body = fetchSeriesBody(seriesNum, request);
        JsonNode staticColumns = findField(body, STATIC_COLS_FIELD);

        List<SeriesColumn> columns = new ArrayList<>();
        appendColumns(columns, staticColumns, FIXED_STATIC_COLUMNS_COUNT, Integer.MAX_VALUE);

        return new SeriesColumnsResponse(columns);
    }

    private JsonNode fetchSeriesBody(String seriesNum, HttpServletRequest request) {
        HttpHeaders headers = extractHeaders(request);
        String url = UriComponentsBuilder.fromUriString(seriesServiceUrl)
                .path(seriesDataPath)
                .buildAndExpand(seriesNum)
                .toUriString();

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                Object.class
        );

        return objectMapper.valueToTree(response.getBody());
    }

    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames == null) {
            return headers;
        }

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, Collections.list(request.getHeaders(headerName)));
        }

        return headers;
    }

    private void appendColumns(List<SeriesColumn> target, JsonNode sourceColumns, int startIndex, int maxCount) {
        if (!sourceColumns.isArray()) {
            return;
        }

        int appendedInIteration;
        for (int i = startIndex, appended = 0; i < sourceColumns.size() && appended < maxCount; i++, appended += appendedInIteration) {
            appendedInIteration = 0;
            String value = extractColumnValue(sourceColumns, i);
            if (value == null) {
                continue;
            }

            target.add(new SeriesColumn(value, value, COLUMN_TYPE_STRING, isVerticalOrientation(value), MAIN_COLUMN_WIDTH,
                    IS_READONLY));
            appendedInIteration = 1;
        }
    }

    private boolean isVerticalOrientation(String columnName) {
        return !NON_VERTICAL_COLUMNS.contains(columnName);
    }

    private JsonNode findField(JsonNode node, String fieldName) {
        JsonNode result = objectMapper.getNodeFactory().missingNode();

        if (node != null && !node.isMissingNode() && !node.isNull()) {
            if (node.isObject() && node.has(fieldName)) {
                result = node.get(fieldName);
            } else {
                for (JsonNode child : node) {
                    JsonNode field = findField(child, fieldName);
                    if (!field.isMissingNode()) {
                        result = field;
                        break;
                    }
                }
            }
        }

        return result;
    }

    private String extractColumnValue(JsonNode columns, int index) {
        String result = null;

        if (index < columns.size()) {
            JsonNode value = columns.get(index);
            if (isPresentNode(value)) {
                result = extractNodeValue(value);
            }
        }

        return result;
    }

    private boolean isPresentNode(JsonNode value) {
        return value != null && !value.isNull() && !value.isMissingNode();
    }

    private String extractNodeValue(JsonNode value) {
        String result = null;

        if (value.isValueNode()) {
            String text = value.asText();
            result = text.isBlank() ? null : text;
        } else if (value.isObject()) {
            result = extractFromObjectNode(value);
        }

        return result;
    }

    private String extractFromObjectNode(JsonNode value) {
        String result = null;

        if (value.hasNonNull(NAME_FIELD)) {
            result = value.get(NAME_FIELD).asText();
        } else if (value.hasNonNull(TITLE_FIELD)) {
            result = value.get(TITLE_FIELD).asText();
        }

        return result;
    }
}
