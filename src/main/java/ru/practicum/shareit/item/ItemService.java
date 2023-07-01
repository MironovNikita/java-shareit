package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto);

    CommentDto comment(long userId, long itemId, CommentDto commentDto);

    ItemDto get(long itemId, long userId);

    List<ItemDto> getByUserId(long userId, Pageable pageable);

    List<ItemDto> getBySearchText(String text, Pageable pageable);

    void delete(long id);
}
