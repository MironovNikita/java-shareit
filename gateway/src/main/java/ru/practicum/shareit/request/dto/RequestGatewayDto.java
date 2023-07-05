package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestGatewayDto {
    @NotBlank(groups = Create.class, message = "Описание запроса не может быть пустым!")
    @Size(groups = Create.class, min = 5, max = 500, message = "Описание запроса должно содержать от 5 до 500 символов")
    private String description;
}
