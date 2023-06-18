package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.validation.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestHeader(HEADER_USER_ID) long userId,
                                 @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        log.info("Запрос на аренду вещи с id {} пользователем с id {}", bookingDto.getItemId(), userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking update(@RequestHeader(HEADER_USER_ID) long ownerId,
                                 @PathVariable long bookingId,
                                 @RequestParam boolean approved) {
        log.info("Запрос на обновление бронирования с id {} пользователем с id {}", bookingId, ownerId);
        return bookingService.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking getById(@RequestHeader(HEADER_USER_ID) long userId,
                                  @PathVariable long bookingId) {
        log.info("Запрос на получение бронирования по ID: {}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getBookingsByBookerId(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(HEADER_USER_ID) long bookerId) {
        log.info("Запрос на получение списка всех бронирований пользователя по ID: {}", bookerId);
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getItemBookingsByOwnerId(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(HEADER_USER_ID) long ownerId) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя с ID: {}", ownerId);
        return bookingService.getItemBookingsByOwnerId(ownerId, state);
    }
}
