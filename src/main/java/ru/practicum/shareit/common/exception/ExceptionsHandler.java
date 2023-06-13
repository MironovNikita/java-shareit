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
    @ExceptionHandler({ObjectNotFoundException.class, SelfItemBookingException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleObjectNotFoundAndSelfItemBookingException(RuntimeException exception) {
        log.error("404 - {}", exception.getMessage());
        return "404 - " + exception.getMessage();
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateEmailException(DuplicateEmailException exception) {
        log.error("409 - {}", exception.getMessage());
        return "409 - " + exception.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("400 - Ошибка валидации поля " + exception.getFieldError());
        return "Ошибка валидации полей объекта " + exception.getObjectName() + "\n" +
                "Сообщение: " + exception.getFieldError();
    }

    @ExceptionHandler(BookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBookingException(BookingException exception) {
        log.error("400 - {}", exception.getMessage());
        return "400 - " + exception.getMessage();
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,String> handleUnsupportedStateException(UnsupportedStateException exception) {
        log.error("500 - {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(Exception exception) {
        log.error("500 - {}", exception.getMessage());
        return "500 - " + exception.getMessage();
    }
}
