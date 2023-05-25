package ru.practicum.shareit.common.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Пользователь c email " + email + " уже существует!");
    }
}
