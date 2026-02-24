package ru.axenix.smartax.dui.service.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Элемент финальной композиции")
public class CorrectionCompositionItem {

    @Schema(description = "Идентификатор (ключ из composition map)")
    private String id;

    @Schema(description = "Отображаемое имя (ключ из composition map)")
    private String name;

}