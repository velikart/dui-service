package ru.axenix.smartax.dui.service.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Ошибки приложения.
 *
 * @author Velikanov Artyom.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorDescription {

    USER_NAME_NOT_FOUND("USER_001", "Не удалось получить данные текущего пользователя",
            HttpStatus.BAD_REQUEST),
    COLLECTION_NOT_FOUND("COLLECTION_001", "Коллекция не найдена",
            HttpStatus.BAD_REQUEST),
    EXPORT_COLLECTION_ERROR("COLLECTION_002", "Не удалось выполнить экспорт коллекции",
            HttpStatus.INTERNAL_SERVER_ERROR),
    COLLECTION_TITLE_ALREADY_EXISTS("COLLECTION_003", "Коллекция с данный названием уже существует",
            HttpStatus.BAD_REQUEST),
    PAGE_NOT_FOUND("PAGE_001", "Страница не найдена",
            HttpStatus.BAD_REQUEST),
    MOCK_NOT_FOUND("MOCK_001", "Мок не найдена",
            HttpStatus.BAD_REQUEST),
    TEMPLATE_NOT_FOUND("TEMPLATE_001", "Шаблон не найден",
            HttpStatus.BAD_REQUEST),
    TEMPLATE_PAGE_NOT_FOUND("TEMPLATE_002", "Файл страницы шаблона не найден",
            HttpStatus.BAD_REQUEST),
    TEMPLATE_IMAGE_NOT_FOUND("TEMPLATE_003", "Файл изображения шаблона не найден",
            HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS("SYSTEM_401", "Неавторизованный доступ", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("SYSTEM_403", "Недостаточно прав для доступа к ресурсу", HttpStatus.FORBIDDEN),
    HANDLER_NOT_FOUND("SYSTEM_404", "HANDLER_NOT_FOUND", HttpStatus.NOT_FOUND),
    UNKNOWN_ERROR("SYSTEM_500", "Неизвестная ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYSTEM_503", "Сервис недоступен", HttpStatus.SERVICE_UNAVAILABLE);

    /**
     * Код ошибки приложения.
     */
    private final String code;

    /**
     * Сообщение ошибки приложения.
     */
    private final String message;

    /**
     * Код HTTP.
     */
    private final HttpStatus httpStatus;

    /**
     * Метод, выбрасывающий ошибку приложения.
     *
     * @throws ApplicationException ошибка, которую выбрасывает метод.
     */
    public void throwException() throws ApplicationException {
        throw exception();
    }

    /**
     * Метод, выбрасывающий ошибку, если объект == null.
     *
     * @param obj объект для проверки на null.
     */
    public void throwIfNull(Object obj) {
        if (obj == null) {
            throw exception();
        }
    }

    /**
     * Метод, выбрасывающий ошибку, если условие истинно.
     *
     * @param condition условие для проверки.
     */
    public void throwIfTrue(boolean condition) {
        if (condition) {
            throw exception();
        }
    }

    /**
     * Метод, выбрасывающий ошибку, если условие ложно.
     *
     * @param condition условие для проверки.
     */
    public void throwIfFalse(boolean condition) {
        if (!condition) {
            throw exception();
        }
    }

    public ApplicationError createApplicationError() {
        return new ApplicationError(this.code, this.message, null, this.httpStatus);
    }

    public ApplicationException exception() {
        return new ApplicationException(createApplicationError());
    }

}
