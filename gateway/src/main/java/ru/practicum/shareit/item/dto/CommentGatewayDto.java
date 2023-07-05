package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentGatewayDto {
    @NotBlank(groups = Create.class, message = "Текст не должен быть пустым!")
    @Size(groups = Create.class, message = "Текст не должен быть менее 1 символа и более 1000 символов!",
            max = 1000, min = 1)
    private String text;
}
