package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.pagination.Pagination;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(HEADER_USER_ID) long userId,
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
    public ItemDto update(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId,
                           @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление предмета {} для пользователя с id {}", itemDto.getName(), userId);
        return itemService.update(id, userId, itemDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@PathVariable long id, @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Запрос на получение предмета по ID: {}", id);
        return itemService.get(id, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByUserId(@RequestHeader(HEADER_USER_ID) long userId,
                                    @RequestParam(value = "from", required = false)
                                    @PositiveOrZero(message = "Минимальное значение индекса: 0") Integer from,
                                    @RequestParam(value = "size", required = false)
                                    @Positive(message = "Минимальное количество элементов: 1")
                                    @Max(value = 20, message = "Максимальное количество элементов: 20") Integer size) {
        log.info("Запрос на получение предметов пользователя по ID: {}", userId);
        return itemService.getByUserId(userId, Pagination.splitByPages(from, size));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getBySearchText(@RequestParam(required = false) String text,
                                    @RequestParam(value = "from", required = false)
                                    @PositiveOrZero(message = "Минимальное значение индекса: 0") Integer from,
                                    @RequestParam(value = "size", required = false)
                                    @Positive(message = "Минимальное количество элементов: 1")
                                    @Max(value = 20, message = "Максимальное количество элементов: 20") Integer size) {
        log.info("Запрос на получение предметов по поисковому запросу: {}", text);
        return itemService.getBySearchText(text, Pagination.splitByPages(from, size));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление предмета по ID: {}", id);
        itemService.delete(id);
    }
}
