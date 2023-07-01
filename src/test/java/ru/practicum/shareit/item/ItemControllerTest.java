package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemService itemService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private ItemController itemController;

    @Test
    @DisplayName("Проверка метода создания вещи")
    void checkCreateShouldCreateItemDto() throws Exception {
        User user = TestData.createTestUser(1L);
        Item item = TestData.createTestItem(1L, true, user);
        ItemDto someItem = itemMapper.transformItemToItemDto(item);
        ItemDto createItemDto = TestData.createTestItemDto(true, 1L);

        when(itemService.create(1L, createItemDto)).thenReturn(someItem);

        mockMvc.perform(post("/items").header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(someItem)));

        verify(itemService, times(1)).create(1L, createItemDto);
    }

    @Test
    @DisplayName("Проверка метода комментирования вещи")
    void checkCommentShouldReturnCreatedComment() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        CommentDto commentDto = new CommentDto(null, "Комментарий", null, null);
        CommentDto createdComment = new CommentDto(1L, "Комментарий", "Имя автора",
                LocalDateTime.now());

        when(itemService.comment(userId, itemId, commentDto)).thenReturn(createdComment);

        mockMvc.perform(post(String.format("/items/%d/comment", itemId)).header(HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                        .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(createdComment)));
        verify(itemService, times(1)).comment(userId, itemId, commentDto);
    }

    @Test
    @DisplayName("Проверка метода обновления предмета")
    void checkUpdateShouldReturnUpdateItemDto() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        ItemDto itemDto = itemMapper.transformItemToItemDto(TestData.createTestItem(itemId, true, user));
        ItemDto updatedDto = TestData.createTestItemDto(true, 1L);

        when(itemService.update(itemId, userId, updatedDto)).thenReturn(itemDto);

        mockMvc.perform(patch(String.format("/items/%d", itemId)).header(HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
        verify(itemService, times(1)).update(itemId, userId, updatedDto);
    }

    @Test
    @DisplayName("Проверка метода обновления предмета, когда предмета с таким ID нет")
    void checkUpdateShouldThrowObjectNotFoundExceptionIfNonexistentId() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        ItemDto itemDto = itemMapper.transformItemToItemDto(TestData.createTestItem(itemId, true, user));
        ItemDto updatedDto = TestData.createTestItemDto(true, 1L);

        when(itemService.update(itemId, userId, updatedDto)).thenThrow(new ObjectNotFoundException("Предмет", itemId));

        mockMvc.perform(patch(String.format("/items/%d", itemId)).header(HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());
        verify(itemService, times(1)).update(itemId, userId, updatedDto);
    }

    @Test
    @DisplayName("Проверка метода получения предмета по ID")
    void checkGetShouldReturnItemDtoById() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        ItemDto someItem = itemMapper.transformItemToItemDto(TestData.createTestItem(itemId, true, user));

        when(itemService.get(itemId, userId)).thenReturn(someItem);

        mockMvc.perform(get(String.format("/items/%d", itemId)).header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(someItem)));
        verify(itemService, times(1)).get(itemId, userId);
    }

    @Test
    @DisplayName("Проверка метода получения предмета по несуществующему ID")
    void checkGetShouldThrowObjectNotFoundExceptionIfNonexistentId() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        ItemDto someItem = itemMapper.transformItemToItemDto(TestData.createTestItem(itemId, true, user));

        when(itemService.get(itemId, userId)).thenThrow(new ObjectNotFoundException("Предмет", itemId));

        mockMvc.perform(get(String.format("/items/%d", itemId)).header(HEADER_USER_ID, userId))
                .andExpect(status().isNotFound());
        verify(itemService, times(1)).get(itemId, userId);
    }

    @Test
    @DisplayName("Проверка метода получения списка предметов по ID пользователя")
    void checkGetByUserIdShouldReturnItemDtoListByUserId() throws Exception {
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        List<ItemDto> userItems = Stream.of(
                TestData.createTestItem(1L, true, user),
                TestData.createTestItem(2L, true, user),
                TestData.createTestItem(3L, true, user)
        ).map(itemMapper::transformItemToItemDto).collect(Collectors.toList());

        when(itemService.getByUserId(userId, null)).thenReturn(userItems);

        mockMvc.perform(get("/items").header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userItems)));
        verify(itemService, times(1)).getByUserId(userId, null);
    }

    @Test
    @DisplayName("Проверка метода получения списка предметов по несуществующему ID пользователя")
    void checkGetByUserIdShouldThrowObjectNotFoundExceptionIfNonexistentId() throws Exception {
        long userId = 1L;

        when(itemService.getByUserId(anyLong(), any())).thenThrow(new ObjectNotFoundException("Пользователь", userId));

        mockMvc.perform(get("/items").header(HEADER_USER_ID, userId))
                .andExpect(status().isNotFound());
        verify(itemService, times(1)).getByUserId(anyLong(), any());
    }

    @Test
    @DisplayName("Проверка метода получения списка предметов по некорректному ID пользователя")
    void checkGetByUserIdShouldReturnInternalServerErrorStatus() throws Exception {
        mockMvc.perform(get("/items").header(HEADER_USER_ID, "text25"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка метода поиска предметов по тексту")
    void checkGetBySearchTextShouldReturnItemDtoListBySearchText() throws Exception {
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        List<ItemDto> searchedItems = Stream.of(
                TestData.createTestItem(1L, true, user),
                TestData.createTestItem(2L, true, user),
                TestData.createTestItem(3L, true, user)
        ).map(itemMapper::transformItemToItemDto).collect(Collectors.toList());

        when(itemService.getBySearchText(anyString(), any())).thenReturn(searchedItems);

        mockMvc.perform(get("/items/search").queryParam("text", "some text"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(searchedItems)));
        verify(itemService, times(1)).getBySearchText(anyString(), any());
    }

    @Test
    @DisplayName("Проверка метода поиска предметов по пустому тексту")
    void checkGetBySearchTextShouldReturnEmptyItemDtoListByEmptySearchText() throws Exception {
        when(itemService.getBySearchText(anyString(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search").queryParam("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
        verify(itemService, times(1)).getBySearchText(anyString(), any());
    }

    @Test
    @DisplayName("Проверка метода удаления вещи")
    void checkDeleteShouldDeleteItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        ItemDto itemDto = itemMapper.transformItemToItemDto(TestData.createTestItem(itemId, true, user));

        mockMvc.perform(delete(String.format("/items/%d", itemId))).andExpect(status().isOk());
        verify(itemService, times(1)).delete(itemId);
    }

    @Test
    @DisplayName("Проверка метода удаления вещи по несуществующему ID")
    void checkDeleteShouldThrowObjectNotFoundExceptionIfNonexistentId() throws Exception {
        long itemId = 9999L;

        doThrow(new ObjectNotFoundException("Предмет", itemId)).when(itemService).delete(itemId);

        mockMvc.perform(delete(String.format("/items/%d", itemId))).andExpect(status().isNotFound());
        verify(itemService, times(1)).delete(itemId);
    }
}