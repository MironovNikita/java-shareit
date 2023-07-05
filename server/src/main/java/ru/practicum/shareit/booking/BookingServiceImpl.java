package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.common.exception.BookingException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.exception.SelfItemBookingException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Booking create(BookingDto bookingDto, long userId) {
        User booker = userService.get(userId);
        Item item = itemMapper.transformItemDtoToItem(itemService.get(bookingDto.getItemId(), userId));

        boolean isAvailable = item.getAvailable();
        if (!isAvailable) {
            log.error("Server: Вещь {} сейчас недоступна!", item.getName());
            throw new BookingException(String.format("Вещь %s сейчас недоступна!", item.getName()));
        }

        if (item.getOwner().getId() == userId) {
            log.error("Server: Невозможно забронировать свою собственную вещь!");
            throw new SelfItemBookingException();
        }

        Booking booking = bookingMapper.transformBookingDtoToBooking(bookingDto);

        boolean isEndTimeBeforeStartTime = booking.getEnd().isBefore(booking.getStart());
        boolean isStartTimeEqualsEndTime = booking.getStart().equals(booking.getEnd());

        if (isEndTimeBeforeStartTime || isStartTimeEqualsEndTime) {
            log.error("Server: Ошибка бронирования! Неверно указаны даты!");
            throw new BookingException("Ошибка бронирования! Неверно указаны даты!");
        }

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking update(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Server: Бронирование с ID {} не найдено!", bookingId);
                    return new ObjectNotFoundException("Бронирование", bookingId);
                });

        boolean isOwnerOfItem = booking.getItem().getOwner().getId() == ownerId;

        if (!isOwnerOfItem) {
            log.error("Server: Владелец с ID {} не найден!", ownerId);
            throw new ObjectNotFoundException("Владелец", ownerId);
        }

        boolean isStatusApprovedOrRejected = booking.getStatus() == BookingStatus.APPROVED ||
                booking.getStatus() == BookingStatus.REJECTED;

        if (isStatusApprovedOrRejected) {
            log.error("Server: Бронирование уже подтверждено или отклонено!");
            throw new BookingException("Бронирование уже подтверждено или отклонено!");
        }

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);

        return booking;
    }

    @Override
    public Booking getById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Server: Бронирование с ID {} не найдено!", bookingId);
                    return new ObjectNotFoundException("Бронирование", bookingId);
                });

        boolean isOwner = booking.getItem().getOwner().getId() == userId;
        boolean isBooker = booking.getBooker().getId() == userId;

        if (!(isOwner || isBooker)) {
            log.error("Server: Бронирование с ID {} не найдено!", bookingId);
            throw new ObjectNotFoundException("Бронирование", bookingId);
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingsByBookerId(long bookerId, BookingState state, Pageable pageable) {
        userService.get(bookerId);

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now(),
                        pageable);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now(),
                        pageable);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.REJECTED, pageable);
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
        }
    }

    @Override
    public List<Booking> getItemBookingsByOwnerId(long ownerId, BookingState state, Pageable pageable) {
        userService.get(ownerId);

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(),
                        pageable);
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED, pageable);
            default:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        }
    }
}
