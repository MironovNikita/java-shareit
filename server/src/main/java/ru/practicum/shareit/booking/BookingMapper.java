package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.dto.BookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking transformBookingDtoToBooking(BookingDto bookingDto);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDatesDto transformBookingToBookingDto(Booking booking);
}
