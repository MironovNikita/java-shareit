package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(long userId, ItemDto itemDto);

    Item update(long itemId, long userId, ItemDto itemDto);

    CommentDto comment(long userId, long itemId, CommentDto commentDto);

    Item get(long itemId, long userId);

    List<Item> getByUserId(long userId);

    List<Item> getBySearchText(String text);

    void delete(long id);
}
