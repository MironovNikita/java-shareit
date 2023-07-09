package ru.practicum.shareit.common.exception;

import lombok.extern.slf4j.Slf4j;
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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("400 - Ошибка валидации поля {}", exception.getFieldError());
        return String.format("Ошибка валидации полей объекта %s%n Сообщение: %s", exception.getObjectName(),
                exception.getFieldError());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConversionFailedException(final IllegalArgumentException exception) {
        String exceptionMessage = exception.getLocalizedMessage();
        log.error("400 - Ошибка при обработке запроса: Unknown state: {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}