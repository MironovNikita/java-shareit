package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingDto {
    @NotNull(groups = {Create.class}, message = "Необходимо указать id вещи для бронирования")
    Long itemId;
    @NotNull(groups = {Create.class}, message = "Необходимо указать начало бронирования")
    LocalDateTime start;
    @NotNull(groups = {Create.class}, message = "Необходимо указать конец бронирования")
    LocalDateTime end;
}
