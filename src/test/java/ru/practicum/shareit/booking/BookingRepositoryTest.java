package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.common.pagination.Pagination;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием ALL")
    void check_getBookingsByBookerId_shouldReturnAllBookingsByBookerId() {
        long userId = 1L;
        User booker = TestData.createTestUser(userId);
        userRepository.save(booker);
        long ownerId = 2L;
        User owner = TestData.createTestUser(ownerId);
        owner.setEmail("another@test.ru");
        userRepository.save(owner);

        Item item = new Item(1L, "Test", "Description", true,
                owner, null, null, null, null);
        itemRepository.save(item);

        LocalDateTime start1 = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking1 = new Booking(1L, start1, end1, item, booker, BookingStatus.APPROVED);

        LocalDateTime start2 = LocalDateTime.of(2123, 1, 1, 9, 0).plusDays(2);
        LocalDateTime end2 = LocalDateTime.of(2123, 2, 1, 9, 0).plusDays(3);
        Booking booking2 = new Booking(2L, start2, end2, item, booker, BookingStatus.APPROVED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        System.out.println(bookingRepository.findAll());

        List<Booking> userBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(),
                Pagination.splitByPages(0, 20));

        assertThat(userBookings.size()).isEqualTo(2);
        assertTrue(userBookings.contains(booking1));
        assertTrue(userBookings.contains(booking2));
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID собственника с условием ALL")
    void check_getBookingsByBookerId_shouldReturnAllBookingsByOwnerId() {
        long userId = 1L;
        User booker = TestData.createTestUser(userId);
        userRepository.save(booker);
        long ownerId = 2L;
        User owner = TestData.createTestUser(ownerId);
        owner.setEmail("another@test.ru");
        userRepository.save(owner);

        Item item = new Item(1L, "Test", "Description", true,
                owner, null, null, null, null);
        itemRepository.save(item);

        LocalDateTime start1 = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking1 = new Booking(1L, start1, end1, item, booker, BookingStatus.APPROVED);

        LocalDateTime start2 = LocalDateTime.of(2123, 1, 1, 9, 0).plusDays(2);
        LocalDateTime end2 = LocalDateTime.of(2123, 2, 1, 9, 0).plusDays(3);
        Booking booking2 = new Booking(2L, start2, end2, item, booker, BookingStatus.APPROVED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        System.out.println(bookingRepository.findAll());

        List<Booking> userBookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId(),
                Pagination.splitByPages(0, 20));

        assertThat(userBookings.size()).isEqualTo(2);
        assertTrue(userBookings.contains(booking1));
        assertTrue(userBookings.contains(booking2));
    }
}
