package ru.axenix.smartax.dui.service.integration.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.axenix.smartax.dui.service.integration.dto.SeriesColumnsResponse;

public interface SeriesColumnsService {

    SeriesColumnsResponse getColumns(String seriesNum, HttpServletRequest request);

    SeriesColumnsResponse getMainColumns(String seriesNum, HttpServletRequest request);

    SeriesColumnsResponse getAdditionalColumns(String seriesNum, HttpServletRequest request);
}
