package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@Builder
@With
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest itemRequest;
}
