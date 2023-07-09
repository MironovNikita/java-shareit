package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.pagination.Pagination;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.common.constants.HeaderConstants.HEADER_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(HEADER_USER_ID) long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Server: Запрос на создание предмета {} для пользователя с id {}", itemDto.getName(), userId);
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto comment(@RequestHeader(HEADER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody CommentDto commentDto) {
        log.info("Server: Запрос на создание комментария пользователем с ID {}", userId);
        return itemService.comment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Server: Запрос на обновление предмета {} для пользователя с id {}", itemDto.getName(), userId);
        return itemService.update(id, userId, itemDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Server: Запрос на получение предмета по ID: {}", id);
        return itemService.get(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByUserId(@RequestHeader(HEADER_USER_ID) long userId,
                                     @RequestParam(value = "from", required = false) Integer from,
                                     @RequestParam(value = "size", required = false) Integer size) {
        log.info("Server: Запрос на получение предметов пользователя по ID: {}", userId);
        return itemService.getByUserId(userId, Pagination.splitByPages(from, size));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getBySearchText(@RequestParam(required = false) String text,
                                         @RequestParam(value = "from", required = false) Integer from,
                                         @RequestParam(value = "size", required = false) Integer size) {
        log.info("Server: Запрос на получение предметов по поисковому запросу: {}", text);
        return itemService.getBySearchText(text, Pagination.splitByPages(from, size));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Server: Запрос на удаление предмета по ID: {}", id);
        itemService.delete(id);
    }
}
