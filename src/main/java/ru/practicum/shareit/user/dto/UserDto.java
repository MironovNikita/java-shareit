package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDtoCreate {
    @NotBlank(message = "Необходимо ввести имя пользователя!")
    String name;
    @Email(message = "Введён некорректный email!")
    @NotBlank(message = "Необходимо ввести email!")
    String email;
}
