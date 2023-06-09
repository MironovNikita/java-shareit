package ru.practicum.shareit.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}