package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.common.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class UserDto {
    @NotBlank(groups = {Create.class}, message = "Необходимо ввести имя пользователя!")
    @Size(groups = {Create.class, Update.class}, min = 1, max = 255,
            message = "Размер имени должен быть от 1 до 255 символов!")
    String name;
    @Email(groups = {Create.class, Update.class}, message = "Введён некорректный email!")
    @NotBlank(groups = {Create.class}, message = "Необходимо ввести email!")
    @Size(groups = {Create.class, Update.class}, min = 5, max = 512,
            message = "Размер email должен быть от 5 до 512 символов!")
    String email;
}
