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
import ru.axenix.smartax.dui.service.integration.dto.DispatchingStatusCountsResponse;
import ru.axenix.smartax.dui.service.integration.service.DispatchingStatusService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DispatchingStatusServiceImpl implements DispatchingStatusService {

    private static final String ITEMS_FIELD = "items";
    private static final String RECORDS_FIELD = "records";
    private static final String STATUS_FIELD = "status";
    private static final String NAME_FIELD = "name";
    private static final String TITLE_FIELD = "title";
    private static final String VALUE_FIELD = "value";
    private static final String FILTER_FIELD = "filter";
    private static final String SORT_FIELD = "sort";
    private static final String PAGER_FIELD = "pager";
    private static final String PAGE_FIELD = "page";
    private static final String LIMIT_FIELD = "limit";
    private static final String FILTER_SERIES_NUM = "series_num";
    private static final String FILTER_STATUS = "productionStageStatusNm";
    private static final String OFFSET_PARAM = "offset";
    private static final String SEARCH_PARAM = "search";
    private static final String SORT_BY_PARAM = "sort_by";
    private static final String SORT_DIR_PARAM = "sort_dir";
    private static final String DIRECTION_FIELD = "direction";
    private static final String TEXT_FIELD = "text";
    private static final String ROUTE_FIELD = "route";
    private static final String CHILDREN_FIELD = "children";
    private static final String URL_PARAMS_FIELD = "urlParams";
    private static final String ID_FIELD = "id";
    private static final int MENU_LIMIT = 20;

    @Value("${client.series-service.url}")
    private String seriesServiceUrl;

    @Value("${client.series-service.dispatching-table-path:/api/v1/dispatching/table}")
    private String dispatchingTablePath;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DispatchingStatusServiceImpl(
            @Qualifier("seriesServiceRestTemplate") RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public DispatchingStatusCountsResponse getStatusCounts(HttpServletRequest request) {
        String url = buildDispatchingUrl(request);
        HttpHeaders headers = extractHeaders(request);

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                Object.class
        );

        JsonNode body = objectMapper.valueToTree(response.getBody());
        Map<String, Integer> statusCounts = countStatuses(body.path(ITEMS_FIELD));

        List<String> items = statusCounts.values().stream()
                .map(String::valueOf)
                .toList();

        int total = statusCounts.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        return new DispatchingStatusCountsResponse(items, String.valueOf(total));
    }


    @Override
    public List<Map<String, Object>> getDispatchingMenu(HttpServletRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(seriesServiceUrl)
                .path(dispatchingTablePath)
                .queryParam(LIMIT_FIELD, MENU_LIMIT)
                .queryParam(OFFSET_PARAM, 0);

        HttpHeaders headers = extractHeaders(request);

        ResponseEntity<Object> response = restTemplate.exchange(
                builder.build().toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                Object.class
        );

        Map<String, Object> responseMap = objectMapper.convertValue(response.getBody(), Map.class);
        Object itemsObject = responseMap == null ? null : responseMap.get(ITEMS_FIELD);
        List<Map<String, Object>> items = asListOfMaps(itemsObject);

        List<Map<String, Object>> children = items.stream()
                .limit(MENU_LIMIT)
                .map(this::toMenuChild)
                .toList();

        return List.of(
                Map.of(
                        TEXT_FIELD, "Исторические данные",
                        ROUTE_FIELD, "/history"
                ),
                Map.of(
                        TEXT_FIELD, "Диспечирование этапов",
                        ROUTE_FIELD, "/main",
                        CHILDREN_FIELD, children
                )
        );
    }

    @Override
    public Map<String, Object> getTableWithRecords(Map<String, Object> payload, HttpServletRequest request) {
        String url = buildDispatchingUrlFromPayload(payload);
        HttpHeaders headers = extractHeaders(request);

        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                Object.class
        );

        Map<String, Object> responseMap = objectMapper.convertValue(response.getBody(), Map.class);
        return normalizeRecordsField(responseMap);
    }

    private Map<String, Object> toMenuChild(Map<String, Object> item) {
        String seriesNum = asString(item.get(FILTER_SERIES_NUM));

        return Map.of(
                TEXT_FIELD, seriesNum,
                ROUTE_FIELD, "/step/" + seriesNum,
                URL_PARAMS_FIELD, Map.of(ID_FIELD, seriesNum)
        );
    }

    private Map<String, Object> normalizeRecordsField(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (source == null) {
            result.put(RECORDS_FIELD, List.of());
            return result;
        }

        result.putAll(source);

        if (result.containsKey(ITEMS_FIELD)) {
            Object records = result.remove(ITEMS_FIELD);
            result.put(RECORDS_FIELD, records);
        }

        result.computeIfAbsent(RECORDS_FIELD, key -> List.of());

        return result;
    }

    private String buildDispatchingUrlFromPayload(Map<String, Object> payload) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(seriesServiceUrl)
                .path(dispatchingTablePath);

        Map<String, Object> safePayload = payload == null ? Collections.emptyMap() : payload;

        Map<String, Object> pager = asMap(safePayload.get(PAGER_FIELD));
        int limit = asInt(pager.get(LIMIT_FIELD), 100);
        int page = asInt(pager.get(PAGE_FIELD), 0);

        builder.queryParam(LIMIT_FIELD, limit);
        builder.queryParam(OFFSET_PARAM, Math.max(page, 0) * limit);

        appendFilterQueryParams(builder, asListOfMaps(safePayload.get(FILTER_FIELD)));
        appendSortQueryParams(builder, asListOfMaps(safePayload.get(SORT_FIELD)));

        return builder.build().toUriString();
    }

    private void appendFilterQueryParams(UriComponentsBuilder builder, List<Map<String, Object>> filters) {
        for (Map<String, Object> filter : filters) {
            String name = asString(filter.get(NAME_FIELD));
            if (FILTER_SERIES_NUM.equals(name)) {
                appendSearchQueryParam(builder, filter);
            }

            if (FILTER_STATUS.equals(name)) {
                appendStatusQueryParams(builder, filter);
            }
        }
    }

    private void appendSearchQueryParam(UriComponentsBuilder builder, Map<String, Object> filter) {
        String search = asString(filter.get(VALUE_FIELD));
        if (!search.isBlank()) {
            builder.queryParam(SEARCH_PARAM, search);
        }
    }

    private void appendStatusQueryParams(UriComponentsBuilder builder, Map<String, Object> filter) {
        List<String> statuses = asListOfStrings(filter.get(VALUE_FIELD));
        for (String status : statuses) {
            if (!status.isBlank()) {
                builder.queryParam(STATUS_FIELD, status);
            }
        }
    }

    private void appendSortQueryParams(UriComponentsBuilder builder, List<Map<String, Object>> sorts) {
        if (sorts.isEmpty()) {
            return;
        }

        Map<String, Object> sort = sorts.get(0);
        String sortBy = asString(sort.get(NAME_FIELD));
        if (!sortBy.isBlank()) {
            builder.queryParam(SORT_BY_PARAM, sortBy);
        }

        String sortDir = asString(sort.get(DIRECTION_FIELD));
        if (!sortDir.isBlank()) {
            builder.queryParam(SORT_DIR_PARAM, sortDir);
        }
    }

    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> mapValue) {
            return objectMapper.convertValue(mapValue, Map.class);
        }

        return Collections.emptyMap();
    }

    private List<Map<String, Object>> asListOfMaps(Object value) {
        if (!(value instanceof List<?> listValue)) {
            return List.of();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : listValue) {
            if (item instanceof Map<?, ?> mapItem) {
                result.add(asMap(mapItem));
            }
        }

        return result;
    }

    private List<String> asListOfStrings(Object value) {
        if (value instanceof List<?> listValue) {
            return listValue.stream()
                    .map(this::asString)
                    .toList();
        }

        String single = asString(value);
        return single.isBlank() ? List.of() : List.of(single);
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int asInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return value == null ? defaultValue : Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private String buildDispatchingUrl(HttpServletRequest request) {
        String baseUrl = UriComponentsBuilder.fromUriString(seriesServiceUrl)
                .path(dispatchingTablePath)
                .build()
                .toUriString();

        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return baseUrl;
        }

        return baseUrl + "?" + queryString;
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

    private Map<String, Integer> countStatuses(JsonNode itemsNode) {
        Map<String, Integer> result = new LinkedHashMap<>();

        if (!itemsNode.isArray()) {
            return result;
        }

        for (JsonNode itemNode : itemsNode) {
            String status = extractStatus(itemNode);
            if (status == null || status.isBlank()) {
                continue;
            }

            result.merge(status, 1, Integer::sum);
        }

        return result;
    }

    private String extractStatus(JsonNode itemNode) {
        String result = null;
        JsonNode statusNode = itemNode.path(STATUS_FIELD);

        if (statusNode.isValueNode()) {
            result = statusNode.asText();
        } else if (statusNode.isObject()) {
            if (statusNode.hasNonNull(VALUE_FIELD)) {
                result = statusNode.get(VALUE_FIELD).asText();
            } else if (statusNode.hasNonNull(NAME_FIELD)) {
                result = statusNode.get(NAME_FIELD).asText();
            } else if (statusNode.hasNonNull(TITLE_FIELD)) {
                result = statusNode.get(TITLE_FIELD).asText();
            }
        }

        return result;
    }
}
