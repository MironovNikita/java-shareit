package ru.practicum.shareit.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionsHandler {
    @ExceptionHandler({ObjectNotFoundException.class, SelfItemBookingException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleObjectNotFoundAndSelfItemBookingException(RuntimeException exception) {
        log.error("404 - {}", exception.getMessage());
        return String.format("404 - %s", exception.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateEmailException(DuplicateEmailException exception) {
        log.error("409 - {}", exception.getMessage());
        return String.format("409 - %s", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("400 - Ошибка валидации поля {}", exception.getFieldError());
        return String.format("Ошибка валидации полей объекта %s%n Сообщение: %s", exception.getObjectName(),
                exception.getFieldError());
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBookingException(BookingException exception) {
        log.error("400 - {}", exception.getMessage());
        return String.format("400 - %s", exception.getMessage());
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConversionFailedException(final ConversionFailedException exception) {
        String exceptionMessage = exception.getLocalizedMessage();
        log.error("400 - Ошибка при обработке запроса: {} Unknown state: {}", exceptionMessage,
                exception.getValue().toString());
        return Map.of("error", String.format("Unknown state: %s", exception.getValue().toString()));
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,String> handleUnsupportedStateException(UnsupportedStateException exception) {
        log.error("500 - {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}