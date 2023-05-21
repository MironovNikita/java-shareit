package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;

@Value
public class UserDtoUpdate {
    String name;
    @Email(message = "Введён некорректный email!")
    String email;
}
