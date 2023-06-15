package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.common.validation.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class BookingDto {
    @NotNull(groups = {Create.class}, message = "Необходимо указать id вещи для бронирования")
    Long itemId;
    @NotNull(groups = {Create.class}, message = "Необходимо указать начало бронирования")
    @FutureOrPresent(groups = Create.class, message = "Невозможно забронировать вещь в прошлом. Пока невозможно :)")
    LocalDateTime start;
    @NotNull(groups = {Create.class}, message = "Необходимо указать конец бронирования")
    @Future(groups = Create.class, message = "Время окончания бронирования должно быть после его начала")
    LocalDateTime end;
    BookingStatus status;
}
