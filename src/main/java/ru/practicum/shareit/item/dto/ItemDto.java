package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class ItemDto {
    @NotBlank(groups = {Create.class}, message = "Необходимо название вещи!")
    @Size(groups = Create.class, min = 1, max = 255, message = "Название вещи должно быть от 1 до 255 символов!")
    String name;
    @NotBlank(groups = {Create.class}, message = "Необходимо описание вещи!")
    @Size(groups = Create.class, min = 1, max = 500, message = "Описание вещи должно быть от 1 до 500 символов!")
    String description;
    @NotNull(groups = {Create.class}, message = "Необходим статус вещи!")
    Boolean available;
}
