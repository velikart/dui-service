package ru.axenix.smartax.dui.service.integration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.axenix.smartax.common.security.Authorization;
import ru.axenix.smartax.dui.service.integration.dto.CorrectionCompositionItem;
import ru.axenix.smartax.dui.service.integration.service.CorrectionPredictTransformService;
import ru.axenix.smartax.web.swagger.annotation.BaseResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/v1/corrections")
@RequiredArgsConstructor
@Tag(name = "Интеграция DUI c series-service", description = "Преобразование ответа corrections/predict")
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = HttpHeaders.AUTHORIZATION, in = SecuritySchemeIn.HEADER)
public class CorrectionPredictController {

    private final CorrectionPredictTransformService correctionPredictTransformService;

    @Operation(
            tags = "Интеграция с series-service",
            summary = "Преобразование corrections/predict",
            description = "Вызывает /api/v1/corrections/predict и возвращает header/solution массивы mass и percent",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/predict/transformed")
    public Map<String, Object> predictTransformed(@RequestBody Object payload, HttpServletRequest request) {
        return correctionPredictTransformService.predictAndTransform(payload, request);
    }


    @Operation(
            tags = "Интеграция с series-service",
            summary = "Преобразование corrections/recalculate",
            description = "Вызывает /api/v1/corrections/recalculate и возвращает header/solution массивы mass и percent",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/recalculate/transformed")
    public Map<String, Object> recalculateTransformed(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        return correctionPredictTransformService.recalculateAndTransform(payload, request);
    }

    @Operation(
            tags = "Интеграция с series-service",
            summary = "final_composition_dp as id-name-value array",
            description = "Вызывает /api/v1/corrections/predict и возвращает "
                    + "solutions.final_composition_dp как массив объектов {id,name,value}",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/predict/final-composition-dp")
    public List<CorrectionCompositionItem> finalCompositionDp(@RequestBody Object payload, HttpServletRequest request) {
        return correctionPredictTransformService.getFinalCompositionDp(payload, request);
    }

    @Operation(
            tags = "Интеграция с series-service",
            summary = "final_composition_gv as id-name-value array",
            description = "Вызывает /api/v1/corrections/predict и возвращает "
                    + "solutions.final_composition_gv как массив объектов {id,name,value}",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    @BaseResponse
    @Authorization
    @PostMapping("/predict/final-composition-gv")
    public List<CorrectionCompositionItem> finalCompositionGv(@RequestBody Object payload, HttpServletRequest request) {
        return correctionPredictTransformService.getFinalCompositionGv(payload, request);
    }
}
