package ru.axenix.smartax.dui.service.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Getter
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Ошибка приложения")
    private final transient ApplicationError error;

    public ApplicationException(ApplicationError error, Throwable cause) {
        super(error != null ? error.getMessage() : null, cause);
        this.error = error;
    }

    @Override
    public String getMessage() {
        return error != null ? error.getMessage() : super.getMessage();
    }
}