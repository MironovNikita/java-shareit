package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingGatewayDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.constants.HeaderGatewayConstants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long userId,
                                         @Validated(Create.class) @RequestBody BookingGatewayDto bookingGatewayDto) {
        log.info("Gateway: Запрос на аренду вещи с id {} пользователем с id {}", bookingGatewayDto.getItemId(), userId);
        return bookingClient.create(userId, bookingGatewayDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@RequestHeader(HEADER_USER_ID) long ownerId,
                                         @PathVariable long bookingId,
                                         @RequestParam boolean approved) {
        log.info("Gateway: Запрос на обновление бронирования с id {} пользователем с id {}", bookingId, ownerId);
        return bookingClient.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getById(@RequestHeader(HEADER_USER_ID) long userId,
                                          @PathVariable long bookingId) {
        log.info("Gateway: Запрос на получение бронирования по ID: {}", bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingsByBookerId(
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestHeader(HEADER_USER_ID) long bookerId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero(message = "Минимальное значение индекса: 0") Integer from,
            @RequestParam(value = "size", defaultValue = "20")
            @Positive(message = "Минимальное количество элементов: 1")
            @Max(value = 20, message = "Максимальное количество элементов: 20") Integer size) {
        BookingState state = stateVerification(stateParam);
        log.info("Gateway: Запрос на получение списка всех бронирований пользователя по ID: {}\n" +
                "Параметр from: {}, параметр size: {}", bookerId, from, size);
        return bookingClient.getBookingsByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemBookingsByOwnerId(
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestHeader(HEADER_USER_ID) long ownerId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero(message = "Минимальное значение индекса: 0") Integer from,
            @RequestParam(value = "size", defaultValue = "20")
            @Positive(message = "Минимальное количество элементов: 1")
            @Max(value = 20, message = "Максимальное количество элементов: 20") Integer size) {
        BookingState state = stateVerification(stateParam);
        log.info("Gateway: Запрос на получение списка бронирований для всех вещей текущего пользователя с ID: {}\n" +
                        "Параметр from: {}, параметр size: {}", ownerId, from, size);
        return bookingClient.getItemBookingsByOwnerId(ownerId, state, from, size);
    }

    private BookingState stateVerification(String stateParam) {
        return BookingState.from(stateParam).orElseThrow(() -> {
            log.error("Неизвестный статус бронирования: {}", stateParam);
            return new IllegalArgumentException(String.format("Unknown state: %s", stateParam));
        });
    }
}