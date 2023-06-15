package ru.practicum.shareit.item.comment;

import lombok.Value;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
public class CommentDto {
    Long id;
    @NotBlank(groups = Create.class, message = "Текст не должен быть пустым!")
    @Size(groups = Create.class, message = "Текст не должен быть менее 1 символа и более 1000 символов!",
            max = 1000, min = 1)
    String text;
    @NotNull(message = "Необходимо имя оставляющего комментарий!")
    String authorName;
    LocalDateTime created;
}