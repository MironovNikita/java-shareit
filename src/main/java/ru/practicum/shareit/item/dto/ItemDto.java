package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class ItemDto {
    @NotBlank(groups = {Create.class}, message = "Необходимо название вещи!")
    String name;
    @NotBlank(groups = {Create.class}, message = "Необходимо описание вещи!")
    String description;
    @NotNull(groups = {Create.class}, message = "Необходим статус вещи!")
    Boolean available;
}
