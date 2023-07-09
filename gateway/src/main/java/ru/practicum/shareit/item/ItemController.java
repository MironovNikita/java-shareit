package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;
import ru.practicum.shareit.item.dto.CommentGatewayDto;
import ru.practicum.shareit.item.dto.ItemGatewayDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

import static ru.practicum.shareit.common.constants.HeaderGatewayConstants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long userId,
                                         @Validated(Create.class) @RequestBody ItemGatewayDto itemGatewayDto) {
        log.info("Gateway: Запрос на создание предмета {} для пользователя с id {}", itemGatewayDto.getName(), userId);
        return itemClient.create(userId, itemGatewayDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId,
                                         @Validated(Update.class) @RequestBody ItemGatewayDto itemGatewayDto) {
        log.info("Gateway: Запрос на обновление предмета {} для пользователя с id {}", itemGatewayDto.getName(), userId);
        return itemClient.update(id, userId, itemGatewayDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> comment(@RequestHeader(HEADER_USER_ID) long userId,
                                          @PathVariable long itemId,
                                          @Validated(Create.class) @RequestBody CommentGatewayDto commentGatewayDto) {
        log.info("Gateway: Запрос на создание комментария пользователем с ID {}", userId);
        return itemClient.comment(userId, itemId, commentGatewayDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Gateway: Запрос на получение предмета по ID: {}", id);
        return itemClient.get(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByUserId(
            @RequestHeader(HEADER_USER_ID) long userId,
            @PositiveOrZero(message = "Минимальное значение индекса: 0")
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive(message = "Минимальное количество элементов: 1")
            @Max(value = 20, message = "Максимальное количество элементов: 20")
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        log.info("Gateway: Запрос на получение предметов пользователя по ID: {}", userId);
        return itemClient.getByUserId(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBySearchText(
            @RequestParam(required = false) String text,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero(message = "Минимальное значение индекса: 0") Integer from,
            @RequestParam(value = "size", defaultValue = "20")
            @Positive(message = "Минимальное количество элементов: 1")
            @Max(value = 20, message = "Максимальное количество элементов: 20") Integer size) {
        log.info("Gateway: Запрос на получение предметов по поисковому запросу: {}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.getBySearchText(text, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> delete(@PathVariable long id) {
        log.info("Запрос на удаление предмета по ID: {}", id);
        return itemClient.delete(id);
    }
}
