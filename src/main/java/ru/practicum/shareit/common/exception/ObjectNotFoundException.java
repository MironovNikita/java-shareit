package ru.practicum.shareit.common.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String object, long id) {
        super(String.format("%s с ID: %s не найден!", object, id));
    }
}
