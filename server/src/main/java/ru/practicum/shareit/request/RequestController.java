package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.pagination.Pagination;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class RequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@RequestHeader(HEADER_USER_ID) long userId,
                             @RequestBody RequestDto requestDto) {
        log.info("Server: Запрос на создание запроса пользователем с id {}", userId);
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getOwnRequests(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Server: Запрос на получение собственных запросов пользователем с id {}", userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getOtherUsersRequests(
            @RequestHeader(HEADER_USER_ID) long userId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        log.info("Server: Запрос пользователя с id {} на получение запросов других пользователей", userId);
        return requestService.getOtherUsersRequests(userId, Pagination.splitByPages(from, size));
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto get(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long requestId) {
        log.info("Server: Запрос на получение запроса с id {} пользователем с id {}", requestId, userId);
        return requestService.get(userId, requestId);
    }
}
