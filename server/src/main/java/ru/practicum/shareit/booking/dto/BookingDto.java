package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
