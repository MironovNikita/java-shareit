package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.request.dto.RequestGatewayDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long userId,
                                         @Validated(Create.class) @RequestBody RequestGatewayDto requestGatewayDto) {
        log.info("Gateway: Запрос на создание запроса пользователем с id {}", userId);
        return requestClient.create(userId, requestGatewayDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Gateway: Запрос на получение собственных запросов пользователем с id {}", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getOtherUsersRequests(
            @RequestHeader(HEADER_USER_ID) long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero(message = "Минимальное значение индекса: 0") Integer from,
            @RequestParam(value = "size", defaultValue = "20")
            @Positive(message = "Минимальное количество элементов: 1")
            @Max(value = 20, message = "Максимальное количество элементов: 20") Integer size) {
        log.info("Запрос пользователя с id {} на получение запросов других пользователей", userId);
        return requestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long requestId) {
        log.info("Запрос на получение запроса с id {} пользователем с id {}", requestId, userId);
        return requestClient.get(userId, requestId);
    }
}
