package ru.practicum.shareit.common.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(String.format("Пользователь c email %s уже существует!", email));
    }
}
