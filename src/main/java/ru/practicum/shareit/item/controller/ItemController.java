package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;
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
    public Item createItem(@RequestHeader(HEADER_USER_ID) long userId,
                           @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание предмета " + itemDto.getName() + " для пользователя с id {}", userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item updateItem(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId,
                           @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление предмета " + itemDto.getName() + " для пользователя с id {}", userId);
        return itemService.update(id, userId, itemDto);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Item getItem(@PathVariable long id) {
        log.info("Запрос на создание предмета получение предмета по ID: {}", id);
        return itemService.get(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getItemsByUserId(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Запрос на получение предметов пользователя по ID: {}", userId);
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getItemsByText(@RequestParam(required = false) String text) {
        log.info("Запрос на получение предметов по поисковому запросу: {}", text);
        return itemService.getBySearchText(text);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItem(@PathVariable long id) {
        log.info("Запрос на удаление предмета по ID: {}", id);
        itemService.delete(id);
    }
}
