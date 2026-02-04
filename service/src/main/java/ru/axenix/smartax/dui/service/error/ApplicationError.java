package ru.axenix.smartax.dui.service.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Модель обработки ошибок приложения.
 *
 * @author Velikanov Artyom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Модель обработки ошибок приложения")
public class ApplicationError {

    /**
     * Код ошибки
     */
    @Schema(description = "Код ошибки")
    private String code;

    /**
     * Сообщение ошибки
     */
    @Schema(description = "Сообщение ошибки")
    private String message;

    /**
     * Стектрейс
     */
    @Schema(description = "Стектрейс")
    private String cause;

    /**
     * Http-код ошибки
     */
    @Schema(description = "Http-код ошибки")
    private HttpStatus httpStatus;
}