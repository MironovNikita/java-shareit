package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.common.validation.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    static final String HEADER_USER_ID = "X-Sharer-User-Id";
    final BookingServiceImpl bookingServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                 @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        log.info("Запрос на аренду вещи с id {} пользователем с id {}", bookingDto.getItemId(), userId);
        return bookingServiceImpl.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking updateBooking(@RequestHeader(HEADER_USER_ID) long ownerId,
                                 @PathVariable long bookingId,
                                 @RequestParam boolean approved) {
        log.info("Запрос на обновление бронирования с id {} пользователем с id {}", bookingId, ownerId);
        return bookingServiceImpl.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking getBookingById(@RequestHeader(HEADER_USER_ID) long userId,
                                  @PathVariable long bookingId) {
        log.info("Запрос на получение бронирования по ID: {}", bookingId);
        return bookingServiceImpl.getById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(HEADER_USER_ID) long bookerId) {
        log.info("Запрос на получение списка всех бронирований пользователя по ID: {}", bookerId);
        return bookingServiceImpl.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getUserItemBookings(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(HEADER_USER_ID) long ownerId) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя с ID: {}", ownerId);
        return bookingServiceImpl.getItemBookingsByOwnerId(ownerId, state);
    }
}
