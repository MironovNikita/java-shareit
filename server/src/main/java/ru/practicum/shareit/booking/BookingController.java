package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.common.pagination.Pagination;

import java.util.List;

import static ru.practicum.shareit.common.constants.HeaderConstants.HEADER_USER_ID;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestHeader(HEADER_USER_ID) long userId,
                          @RequestBody BookingDto bookingDto) {
        log.info("Server: Запрос на аренду вещи с id {} пользователем с id {}", bookingDto.getItemId(), userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking update(@RequestHeader(HEADER_USER_ID) long ownerId,
                                 @PathVariable long bookingId,
                                 @RequestParam boolean approved) {
        log.info("Server: Запрос на обновление бронирования с id {} пользователем с id {}", bookingId, ownerId);
        return bookingService.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Booking getById(@RequestHeader(HEADER_USER_ID) long userId,
                           @PathVariable long bookingId) {
        log.info("Server: Запрос на получение бронирования по ID: {}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getBookingsByBookerId(
            @RequestParam(name = "state", defaultValue = "ALL") BookingState searchState,
            @RequestHeader(HEADER_USER_ID) long bookerId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        log.info("Server: Запрос на получение списка всех бронирований пользователя по ID: {}", bookerId);
        return bookingService.getBookingsByBookerId(bookerId, searchState, Pagination.splitByPages(from, size));
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<Booking> getItemBookingsByOwnerId(
            @RequestParam(name = "state", defaultValue = "ALL") BookingState searchState,
            @RequestHeader(HEADER_USER_ID) long ownerId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        log.info("Server: Запрос на получение списка бронирований для всех вещей текущего пользователя с ID: {}",
                ownerId);
        return bookingService.getItemBookingsByOwnerId(ownerId, searchState, Pagination.splitByPages(from, size));
    }
}
