package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {
    @Spy BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    @DisplayName("Проверка маппинга бронирования в BookingDatesDto")
    void check_transformBookingToBookingDto_shouldBeCorrectTransform() {
        User user = TestData.createTestUser(1L);
        Item item = TestData.createTestItem(1L, true, user);

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        BookingDatesDto bookingDatesDto = bookingMapper.transformBookingToBookingDto(booking);

        assertThat(bookingDatesDto.getBookerId()).isEqualTo(user.getId());
        assertThat(bookingDatesDto.getId()).isEqualTo(1L);
    }
}
