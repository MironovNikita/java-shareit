package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("Проверка создания предмета с ID запроса")
    void checkCreateShouldCreateItemWithRequest() {
        long requestId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Request request = TestData.createTestRequest(requestId, LocalDateTime.now(), user);
        ItemDto itemDto = TestData.createTestItemDto(true, requestId);

        when(userService.get(userId)).thenReturn(user);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto checkDto = itemService.create(userId, itemDto);

        assertTrue(checkDto.getAvailable());
        assertThat(checkDto.getOwner()).isEqualTo(user);
        assertThat(checkDto.getRequestId()).isEqualTo(requestId);
        assertNull(checkDto.getComments());
    }

    @Test
    @DisplayName("Проверка создания предмета с ID несуществующего запроса")
    void checkCreateShouldThrowObjectNotFoundExceptionIfNonexistentRequestId() {
        long requestId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Request request = TestData.createTestRequest(requestId, LocalDateTime.now(), user);
        ItemDto itemDto = TestData.createTestItemDto(true, requestId);

        when(userService.get(userId)).thenReturn(user);
        when(requestRepository.findById(requestId)).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() -> itemService.create(userId, itemDto)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка создания предмета у несуществующего пользователя")
    void checkCreateShouldThrowObjectNotFoundExceptionIfNonexistentUser() {
        long userId = 9999L;
        ItemDto itemDto = TestData.createTestItemDto(true, 1L);

        when(userService.get(userId)).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() -> itemService.create(userId, itemDto)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка обновления статуса предмета")
    void checkUpdateShouldUpdateItemAvailableStatus() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);
        ItemDto itemDto = new ItemDto(null, null, null, false, null, null,
                null, null, null);

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto checkDto = itemService.update(itemId, userId, itemDto);

        assertThat(checkDto.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Проверка обновления описания предмета")
    void checkUpdateShouldUpdateItemDescription() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);
        ItemDto itemDto = new ItemDto(null, null, "Описание", null, null, null,
                null, null, null);

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto checkDto = itemService.update(itemId, userId, itemDto);

        assertThat(checkDto.getDescription()).isEqualTo("Описание");
    }

    @Test
    @DisplayName("Проверка обновления названия предмета")
    void checkUpdateShouldUpdateItemName() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);
        ItemDto itemDto = new ItemDto(null, "Название", null, null, null, null,
                null, null, null);

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto checkDto = itemService.update(itemId, userId, itemDto);

        assertThat(checkDto.getName()).isEqualTo("Название");
    }

    @Test
    @DisplayName("Проверка обновления предмета, если предмет не существует")
    void checkUpdateShouldThrowObjectNotFoundExceptionIfNonexistentItemId() {
        long itemId = 9999L;
        long userId = 1L;
        ItemDto itemDto = new ItemDto(null, "Название", null, null, null, null,
                null, null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(itemId, userId, itemDto))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка обновления предмета, если пользователь (хозяин) не существует")
    void checkUpdateShouldThrowObjectNotFoundExceptionIfNonexistentUserId() {
        long itemId = 1L;
        long userId = 9999L;
        ItemDto itemDto = new ItemDto(null, "Название", null, null, null, null,
                null, null, null);

        when(userService.get(userId)).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() -> itemService.update(itemId, userId, itemDto))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка создания комментария пользователем на вещь")
    void checkCommentShouldCreateCommentToItem() {
        long itemId = 1;
        long userId = 1;
        User user = TestData.createTestUser(userId);
        user.setName("Имя автора");
        Item item = TestData.createTestItem(itemId, true, user);
        CommentDto commentDto = new CommentDto(null, "Комментарий", null, null);
        CommentDto createdComment = new CommentDto(1L, "Комментарий", "Имя автора",
                LocalDateTime.now());

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(new Booking(1L,
                        LocalDateTime.of(2022, 10, 10, 21, 10),
                        LocalDateTime.of(2022, 10, 12, 21, 0), item, user,
                        BookingStatus.APPROVED)));
        when(itemService.comment(userId, itemId, commentDto)).thenReturn(createdComment);

        CommentDto checkComment = itemService.comment(userId, itemId, commentDto);

        assertThat(checkComment.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("Проверка создания комментария пользователем на несуществующую вещь")
    void checkCommentShouldCreateCommentToNonexistentItem() {
        long itemId = 1;
        long userId = 1;
        User user = TestData.createTestUser(userId);
        user.setName("Имя автора");
        Item item = TestData.createTestItem(itemId, true, user);
        CommentDto commentDto = new CommentDto(null, "Комментарий", null, null);
        CommentDto createdComment = new CommentDto(1L, "Комментарий", "Имя автора",
                LocalDateTime.now());

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.comment(itemId, userId, commentDto))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка получения вещи по ID")
    void checkGetShouldReturnItemDtoById() {
        long itemId = 1;
        long userId = 1;
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED))
                .thenReturn(Collections.emptyList());
        when(commentService.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        assertThat(itemService.get(itemId, userId)).isEqualTo(itemMapper.transformItemToItemDto(item));
    }

    @Test
    @DisplayName("Проверка получения вещи по несуществующему ID")
    void checkGetShouldThrowObjectNotFoundExceptionIfNonexistentId() {
        long itemId = 1;
        long userId = 1;
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.get(itemId, userId)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения списка предметов по ID пользователя")
    void checkGetByUserIdShouldReturnItemDtoListByUserId() {
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Item item1 = TestData.createTestItem(1L, true, user);
        Item item2 = TestData.createTestItem(2L, true, user);
        Item item3 = TestData.createTestItem(3L, true, user);
        item1.setComments(Collections.emptyList());
        item2.setComments(Collections.emptyList());
        item3.setComments(Collections.emptyList());
        List<Item> userItems = List.of(item1, item2, item3);
        List<ItemDto> userItemDtoS = userItems.stream()
                        .map(itemMapper::transformItemToItemDto)
                        .collect(Collectors.toList());

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(userId, null)).thenReturn(userItems);

        assertThat(itemService.getByUserId(userId, null)).isEqualTo(userItemDtoS);
    }

    @Test
    @DisplayName("Проверка метода поиска предметов по тексту")
    void checkGetBySearchTextShouldReturnItemListByText() {
        User user = TestData.createTestUser(1L);
        List<Item> searchItems = List.of(
                TestData.createTestItem(1L,true, user),
                TestData.createTestItem(2L,true, user),
                TestData.createTestItem(3L,true, user)
        );
        List<ItemDto> searchDto = searchItems.stream()
                .map(itemMapper::transformItemToItemDto)
                .collect(Collectors.toList());

        when(itemRepository.findAllByText(anyString(), any())).thenReturn(searchItems);

        assertThat(itemService.getBySearchText("some text", null)).isEqualTo(searchDto);
    }

    @Test
    @DisplayName("Проверка метода поиска предметов по пустому тексту")
    void checkGetBySearchTextShouldReturnEmptyListIfSearchTextIsEmpty() {
        assertThat(itemService.getBySearchText("", null)).isEmpty();
    }

    @Test
    @DisplayName("Проверка удаления вещи по ID")
    void checkDeleteShouldDeleteItemById() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        Item item = TestData.createTestItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemService.delete(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("Проверка метода удаления вещи по несуществующему ID")
    void checkDeleteShouldThrowObjectNotFoundExceptionIfNonexistentId() {
        long id = 9999L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.delete(id)).isInstanceOf(ObjectNotFoundException.class);
    }
}