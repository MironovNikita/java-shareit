package ru.practicum.shareit.common.exception;

public class SelfItemBookingException extends RuntimeException {
    public SelfItemBookingException() {
        super("Невозможно забронировать свою собственную вещь!");
    }
}
