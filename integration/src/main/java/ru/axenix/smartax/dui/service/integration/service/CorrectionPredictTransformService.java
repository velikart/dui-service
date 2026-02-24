package ru.axenix.smartax.dui.service.integration.service;

import jakarta.servlet.http.HttpServletRequest;

import ru.axenix.smartax.dui.service.integration.dto.CorrectionCompositionItem;

import java.util.List;
import java.util.Map;

public interface CorrectionPredictTransformService {

    Map<String, Object> predictAndTransform(Object payload, HttpServletRequest request);

    Map<String, Object> recalculateAndTransform(Object payload, HttpServletRequest request);

    List<CorrectionCompositionItem> getFinalCompositionDp(Object payload, HttpServletRequest request);

    List<CorrectionCompositionItem> getFinalCompositionGv(Object payload, HttpServletRequest request);
}