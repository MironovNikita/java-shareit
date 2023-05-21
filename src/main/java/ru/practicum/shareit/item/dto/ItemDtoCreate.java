package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class ItemDtoCreate {
    @NotBlank(message = "Необходимо название вещи!")
    String name;
    @NotBlank(message = "Необходимо описание вещи!")
    String description;
    @NotNull(message = "Необходим статус вещи!")
    Boolean available;
}
