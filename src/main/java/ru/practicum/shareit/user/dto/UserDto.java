package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    @NotBlank(groups = {Create.class}, message = "Необходимо ввести имя пользователя!")
    String name;
    @Email(groups = {Create.class, Update.class}, message = "Введён некорректный email!")
    @NotBlank(groups = {Create.class}, message = "Необходимо ввести email!")
    String email;
}
