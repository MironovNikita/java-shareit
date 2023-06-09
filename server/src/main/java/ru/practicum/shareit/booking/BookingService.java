package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, long userId);

    Booking update(long ownerId, long bookingId, boolean approved);

    Booking getById(long bookingId, long userId);

    List<Booking> getBookingsByBookerId(long bookerId, BookingState state, Pageable pageable);

    List<Booking> getItemBookingsByOwnerId(long ownerId, BookingState state, Pageable pageable);
}
