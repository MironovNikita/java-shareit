package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader(HEADER_USER_ID) long userId,
                           @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание предмета {} для пользователя с id {}", itemDto.getName(), userId);
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto comment(@RequestHeader(HEADER_USER_ID) long userId,
                               @PathVariable long itemId,
                               @Validated(Create.class) @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария пользователем с ID {}", userId);
        return itemService.comment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId,
                           @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление предмета {} для пользователя с id {}", itemDto.getName(), userId);
        return itemService.update(id, userId, itemDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item get(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Запрос на получение предмета по ID: {}", id);
        return itemService.get(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getByUserId(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Запрос на получение предметов пользователя по ID: {}", userId);
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getBySearchText(@RequestParam(required = false) String text) {
        log.info("Запрос на получение предметов по поисковому запросу: {}", text);
        return itemService.getBySearchText(text);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление предмета по ID: {}", id);
        itemService.delete(id);
    }
}
