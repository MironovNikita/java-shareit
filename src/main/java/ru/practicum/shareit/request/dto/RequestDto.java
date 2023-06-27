package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.common.validation.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "Описание запроса не может быть пустым!")
    @Size(groups = Create.class, min = 5, max = 500, message = "Описание запроса должно содержать от 5 до 500 символов")
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
