package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.common.exception.BookingException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userService.get(userId);
        Item item = itemMapper.transformItemDtoToItem(itemDto);
        item.setOwner(user);

        if (itemDto.getRequestId() != null) {
            requestRepository.findById(itemDto.getRequestId()).ifPresentOrElse(item::setRequest, () -> {
                log.error("Запрос с ID {} не найден!", itemDto.getRequestId());
                throw new ObjectNotFoundException("Запрос", itemDto.getRequestId());
            });
        }

        return itemMapper.transformItemToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        User user = userService.get(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Предмет с ID {} не найден!", itemId);
            return new ObjectNotFoundException("Предмет", itemId);
        });

        if (!user.equals(item.getOwner())) {
            log.error("Попытка обновления предмета пользователем с ID {} при собственнике с ID {}", userId,
                    item.getOwner().getId());
            throw new ObjectNotFoundException("Предмет", itemId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.transformItemToItemDto(item);
    }

    @Override
    @Transactional
    public CommentDto comment(long userId, long itemId, CommentDto commentDto) {
        User user = userService.get(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Предмет с ID {} не найден!", itemId);
            return new ObjectNotFoundException("Предмет", itemId);
        });

        List<Booking> userBookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                userId, LocalDateTime.now(), null);

        if (userBookingList.isEmpty()) {
            log.error("Пользователь не может оставить комментарий, т.к. не бронировал данную вещь!");
            throw new BookingException("Пользователь не может оставить комментарий, т.к. не бронировал данную вещь!");
        }

        Comment comment = commentMapper.transformCommentDtoToComment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.transformCommentToCommentDto(commentService.createComment(comment));
    }

    @Override
    public ItemDto get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Предмет с ID {} не найден!", itemId);
            return new ObjectNotFoundException("Предмет", itemId);
        });

        if (item.getOwner().getId() == userId) {
            List<Booking> bookingList = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemId,
                    BookingStatus.APPROVED);

            item.setLastBooking(bookingMapper.transformBookingToBookingDto(getLastBooking(bookingList)));
            item.setNextBooking(bookingMapper.transformBookingToBookingDto(getNextBooking(bookingList)));
        }

        item.setComments(commentService.findAllByItemId(itemId)
                .stream().map(commentMapper::transformCommentToCommentDto)
                .collect(Collectors.toList()));

        return itemMapper.transformItemToItemDto(item);
    }

    private Booking getNextBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    private Booking getLastBooking(List<Booking> bookingList) {
        return bookingList.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    @Override
    public List<ItemDto> getByUserId(long userId, Pageable pageable) {
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageable)
                .stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.transformItemToItemDto(item);
                    List<Booking> bookingList = bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(
                            item.getId(), BookingStatus.APPROVED);

                    itemDto.setLastBooking(bookingMapper.transformBookingToBookingDto(getLastBooking(bookingList)));
                    itemDto.setNextBooking(bookingMapper.transformBookingToBookingDto(getNextBooking(bookingList)));

                    itemDto.setComments(commentService.findAllByItemId(item.getId())
                            .stream().map(commentMapper::transformCommentToCommentDto)
                            .collect(Collectors.toList()));

                    return itemDto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getBySearchText(String text, Pageable pageable) {
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.findAllByText(text, pageable)
                .stream()
                .map(itemMapper::transformItemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            log.error("Предмет с ID {} не найден!", id);
            return new ObjectNotFoundException("Предмет", id);
        });
        itemRepository.deleteById(id);
    }
}