package ru.practicum.shareit.common.exception;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}
