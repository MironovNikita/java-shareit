package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionFailedException;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.common.exception.BookingException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.SelfItemBookingException;
import ru.practicum.shareit.common.exception.UnsupportedStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Mock
    private UserService userService;
    @Mock
    ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Проверка метода создания бронирования")
    void check_create_shouldCreateBooking() {
        long userId = 1L;
        long itemId = 1L;

        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                null);
        User user = TestData.createTestUser(2L);
        Item item = TestData.createTestItem(itemId, true, user);
        ItemDto itemDto = itemMapper.transformItemToItemDto(item);

        when(itemService.get(itemId, userId)).thenReturn(itemDto);
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Booking booking = bookingService.create(bookingDto, userId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getItem()).isEqualTo(item);
    }

    @Test
    @DisplayName("Проверка метода создания бронирования при некорректных датах бронирования")
    void check_create_shouldThrowBookingExceptionIfBookingDatesAreIncorrect() {
        long userId = 1L;
        long itemId = 1L;

        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().minusDays(2),
                null);
        User user = TestData.createTestUser(2L);
        Item item = TestData.createTestItem(itemId, true, user);
        ItemDto itemDto = itemMapper.transformItemToItemDto(item);

        when(itemService.get(itemId, userId)).thenReturn(itemDto);

        assertThatThrownBy(() -> bookingService.create(bookingDto, userId)).isInstanceOf(BookingException.class);
    }

    @Test
    @DisplayName("Проверка метода создания бронирования своей же вещи")
    void check_create_shouldThrowSelfItemBookingExceptionIfUserIdIsEqualToOwnerId() {
        long userId = 1L;
        long itemId = 1L;

        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                null);
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);
        ItemDto itemDto = itemMapper.transformItemToItemDto(item);

        when(itemService.get(itemId, userId)).thenReturn(itemDto);

        assertThatThrownBy(() -> bookingService.create(bookingDto, userId))
                .isInstanceOf(SelfItemBookingException.class);
    }

    @Test
    @DisplayName("Проверка метода создания бронирования при недоступности этой вещи")
    void check_create_shouldThrowBookingExceptionIfItemIsUnavailable() {
        long userId = 1L;
        long itemId = 1L;

        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                null);
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, false, user);
        ItemDto itemDto = itemMapper.transformItemToItemDto(item);

        when(itemService.get(itemId, userId)).thenReturn(itemDto);

        assertThatThrownBy(() -> bookingService.create(bookingDto, userId))
                .isInstanceOf(BookingException.class);
    }

    @Test
    @DisplayName("Проверка метода создания бронирования при несуществующей вещи")
    void check_create_shouldThrowObjectNotFoundExceptionIfNonexistentItemId() {
        long userId = 1L;
        long itemId = 1L;

        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                null);

        when(itemService.get(itemId, userId)).thenThrow(new ObjectNotFoundException("Вещь", itemId));

        assertThatThrownBy(() -> bookingService.create(bookingDto, userId))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода создания бронирования при несуществующем пользователе")
    void check_create_shouldThrowObjectNotFoundExceptionIfNonexistentUserId() {
        long userId = 1L;
        long itemId = 1L;

        BookingDto bookingDto = new BookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                null);

        when(userService.get(userId)).thenThrow(new ObjectNotFoundException("Пользователь", userId));

        assertThatThrownBy(() -> bookingService.create(bookingDto, userId))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода обновления бронирования до утверждения")
    void check_update_shouldUpdateBookingStatusToApproved() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;

        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        booking = bookingService.update(bookingId, userId, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("Проверка метода обновления бронирования, если оно уже было утверждено")
    void check_update_shouldThrowBookingExceptionIfUpdatingAlreadyApprovedStatus() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;

        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(userId, bookingId, true))
                .isInstanceOf(BookingException.class);
    }

    @Test
    @DisplayName("Проверка метода обновления бронирования, если пользователь не собственник")
    void check_update_shouldThrowObjectNotFoundExceptionIfUserIsNotOwnerOfItem() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;

        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(2L, bookingId, true))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода обновления несуществующего бронирования")
    void check_update_shouldThrowObjectNotFoundExceptionIfNonexistentBooking() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.update(userId, bookingId, true))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения бронирования по ID")
    void check_getById_shouldReturnBookingById() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;

        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, item, user, BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(bookingService.getById(bookingId, userId)).isEqualTo(booking);
    }

    @Test
    @DisplayName("Проверка метода получения бронирования по ID, если пользователь ни собственник, ни бронирующий")
    void check_getById_ThrowObjectNotFoundExceptionIfUserIsNotOwnerOrBooker() {
        long userId = 1L;
        long itemId = 1L;
        long bookingId = 1L;

        User user1 = TestData.createTestUser(userId);
        User user2 = TestData.createTestUser(2L);
        Item item = TestData.createTestItem(itemId, true, user1);

        LocalDateTime start = LocalDateTime.of(2123, 1, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2123, 2, 1, 9, 0);
        Booking booking = new Booking(bookingId, start, end, item, user1, BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getById(bookingId, user2.getId()))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения несуществующего бронирования по ID")
    void check_getById_ThrowObjectNotFoundExceptionIfNonexistentBooking() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(bookingId, userId))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием ALL")
    void check_getBookingsByBookerId_shouldUseMethod_findAllByBookerIdOrderByStartDesc() {
        bookingService.getBookingsByBookerId(1L, BookingSearchState.ALL, null);
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием CURRENT")
    void check_getBookingsByBookerId_shouldUseMethod_findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getBookingsByBookerId(1L, BookingSearchState.CURRENT, null);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием PAST")
    void check_getBookingsByBookerId_shouldUseMethod_findAllByBookerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getBookingsByBookerId(1L, BookingSearchState.PAST, null);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием FUTURE")
    void check_getBookingsByBookerId_shouldUseMethod_findAllByBookerIdAndStartAfterOrderByStartDesc() {
        bookingService.getBookingsByBookerId(1L, BookingSearchState.FUTURE, null);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием WAITING")
    void check_getBookingsByBookerId_shouldUseMethod_findAllByBookerIdAndStatusOrderByStartDesc_Waiting() {
        bookingService.getBookingsByBookerId(1L, BookingSearchState.WAITING, null);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований по ID бронирующего с условием REJECTED")
    void check_getBookingsByBookerId_shouldUseMethod_findAllByBookerIdAndStatusOrderByStartDesc_Rejected() {
        bookingService.getBookingsByBookerId(1L, BookingSearchState.REJECTED, null);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственником с условием ALL")
    void check_getItemBookingsByOwnerId_shouldUseMethod_findAllByItemOwnerIdOrderByStartDesc() {
        bookingService.getItemBookingsByOwnerId(1L, BookingSearchState.ALL, null);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственником с условием CURRENT")
    void check_getItemBookingsByOwnerId_shouldUseMethod_findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getItemBookingsByOwnerId(1L, BookingSearchState.CURRENT, null);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственником с условием PAST")
    void check_getItemBookingsByOwnerId_shouldUseMethod_findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getItemBookingsByOwnerId(1L, BookingSearchState.PAST, null);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственником с условием FUTURE")
    void check_getItemBookingsByOwnerId_shouldUseMethod_findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        bookingService.getItemBookingsByOwnerId(1L, BookingSearchState.FUTURE, null);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственником с условием WAITING")
    void check_getItemBookingsByOwnerId_shouldUseMethod_findAllByItemOwnerIdAndStatusOrderByStartDesc_Waiting() {
        bookingService.getItemBookingsByOwnerId(1L, BookingSearchState.WAITING, null);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Проверка метода получения бронирований собственником с условием WAITING")
    void check_getItemBookingsByOwnerId_shouldUseMethod_findAllByItemOwnerIdAndStatusOrderByStartDesc_Rejected() {
        bookingService.getItemBookingsByOwnerId(1L, BookingSearchState.REJECTED, null);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
    }
}