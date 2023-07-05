package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String email;
}
