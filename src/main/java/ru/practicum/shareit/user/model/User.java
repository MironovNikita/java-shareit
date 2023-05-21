package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class User {
    private Long id;
    private String name;
    private String email;
}
