package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class ItemDtoUpdate {
    String name;
    String description;
    Boolean available;
}
