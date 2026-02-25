package ru.axenix.smartax.dui.service.integration.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.axenix.smartax.dui.service.integration.dto.DispatchingStatusCountsResponse;

import java.util.List;
import java.util.Map;

public interface DispatchingStatusService {

    DispatchingStatusCountsResponse getStatusCounts(HttpServletRequest request);

    Map<String, Object> getTableWithRecords(Map<String, Object> payload, HttpServletRequest request);

    List<Map<String, Object>> getDispatchingMenu(HttpServletRequest request);
}