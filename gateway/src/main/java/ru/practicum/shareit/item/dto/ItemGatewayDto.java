package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemGatewayDto {
    @NotBlank(groups = {Create.class}, message = "Необходимо название вещи!")
    @Size(groups = {Create.class, Update.class}, min = 1, max = 255,
            message = "Название вещи должно быть от 1 до 255 символов!")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Необходимо описание вещи!")
    @Size(groups = {Create.class, Update.class}, min = 1, max = 500,
            message = "Описание вещи должно быть от 1 до 500 символов!")
    private String description;
    @NotNull(groups = {Create.class}, message = "Необходим статус вещи!")
    private Boolean available;
    @Positive(groups = Create.class, message = "ID запроса должен быть положительным!")
    private Long requestId;
}
