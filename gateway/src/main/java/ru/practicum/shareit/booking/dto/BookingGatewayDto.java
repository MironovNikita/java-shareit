package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.StartBeforeEndDateValid;
import ru.practicum.shareit.common.validation.Create;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingGatewayDto {
	@NotNull(groups = {Create.class}, message = "Необходимо указать id вещи для бронирования")
	Long itemId;
	@NotNull(groups = {Create.class}, message = "Необходимо указать начало бронирования")
	@FutureOrPresent(groups = Create.class, message = "Невозможно забронировать вещь в прошлом. Пока невозможно :)")
	LocalDateTime start;
	@NotNull(groups = {Create.class}, message = "Необходимо указать конец бронирования")
	@Future(groups = Create.class, message = "Время окончания бронирования должно быть после его начала")
	LocalDateTime end;
}
