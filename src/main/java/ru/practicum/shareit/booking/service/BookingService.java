package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, long userId);

    Booking update(long ownerId, long bookingId, boolean approved);

    Booking getById(long bookingId, long userId);

    List<Booking> getBookingsByBookerId(long bookerId, String state);

    List<Booking> getItemBookingsByOwnerId(long ownerId, String state);
}
