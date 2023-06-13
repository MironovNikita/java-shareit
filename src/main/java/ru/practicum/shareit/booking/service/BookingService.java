package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.BookingException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.SelfItemBookingException;
import ru.practicum.shareit.common.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    BookingMapper bookingMapper;
    UserService userService;
    ItemService itemService;

    public Booking create(BookingDto bookingDto, long userId) {
        User booker = userService.get(userId);
        Item item = itemService.get(bookingDto.getItemId(), userId);

        boolean isAvailable = item.getAvailable();
        if (!isAvailable) {
            log.error("Вещь " + item.getName() + "сейчас недоступна!");
            throw new BookingException("Вещь " + item.getName() + " сейчас недоступна!");
        }

        if (item.getOwner().getId() == userId) {
            log.error("Невозможно забронировать свою собственную вещь!");
            throw new SelfItemBookingException();
        }

        Booking booking = bookingMapper.transformBookingDtoToBooking(bookingDto);

        boolean isStartTimeBeforeNow = booking.getStart().isBefore(LocalDateTime.now());
        boolean isEndTimeBeforeNow = booking.getEnd().isBefore(LocalDateTime.now());
        boolean isEndTimeBeforeStartTime = booking.getEnd().isBefore(booking.getStart());
        boolean isStartTimeEqualsEndTime = booking.getStart().equals(booking.getEnd());

        if (isStartTimeBeforeNow || isEndTimeBeforeNow || isEndTimeBeforeStartTime || isStartTimeEqualsEndTime) {
            log.error("Ошибка бронирования! Неверно указаны даты!");
            throw new BookingException("Ошибка бронирования! Неверно указаны даты!");
        }

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        return bookingRepository.save(booking);
    }

    public Booking update(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование", bookingId));

        boolean isOwnerOfItem = booking.getItem().getOwner().getId() == ownerId;

        if (!isOwnerOfItem) {
            log.error("Владелец с ID {} не найден!", ownerId);
            throw new ObjectNotFoundException("Владелец", ownerId);
        }

        boolean isStatusApprovedOrRejected = booking.getStatus() == BookingStatus.APPROVED ||
                booking.getStatus() == BookingStatus.REJECTED;

        if (isStatusApprovedOrRejected) {
            log.error("Бронирование уже подтверждено или отклонено!");
            throw new BookingException("Бронирование уже подтверждено или отклонено!");
        }

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);

        return bookingRepository.save(booking);
    }

    public Booking getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование", bookingId));

        boolean isOwner = booking.getItem().getOwner().getId() == userId;
        boolean isBooker = booking.getBooker().getId() == userId;

        if (!(isOwner || isBooker)) {
            log.error("Бронирование с ID {} не найдено!", bookingId);
            throw new ObjectNotFoundException("Бронирование", bookingId);
        }

        return booking;
    }

    //Получение списка всех бронирований текущего пользователя.
    public List<Booking> getBookingsByBookerId(long bookerId, String state) {
        userService.get(bookerId);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.REJECTED);
            default:
                throw new UnsupportedStateException();
        }
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    public List<Booking> getItemBookingsByOwnerId(long ownerId, String state) {
        userService.get(ownerId);

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED);
            default:
                throw new UnsupportedStateException();
        }
    }
}
