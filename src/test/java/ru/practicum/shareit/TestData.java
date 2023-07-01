package ru.practicum.shareit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestData {
    public static Item createTestItem(long id, boolean available, User user) {
        return Item.builder()
                .id(id)
                .name("Тестовый предмет")
                .description("Описание тестового предмета")
                .available(available)
                .owner(user)
                .build();
    }

    public static ItemDto createTestItemDto(boolean available, Long requestId) {
        return ItemDto.builder()
                .name("Тестовый предмет")
                .description("Описание тестового предмета")
                .available(available)
                .requestId(requestId)
                .build();
    }

    public static Request createTestRequest(long id, LocalDateTime created, User user) {
        return Request.builder()
                .id(id)
                .description("Описание тестового запроса")
                .user(user)
                .created(created)
                .build();
    }

    public static RequestDto createTestRequestDto(long id) {
        return RequestDto.builder()
                .id(id)
                .description("Описание тестового запроса")
                .created(LocalDateTime.now())
                .build();
    }

    public static User createTestUser(long id) {
        return User.builder()
                .id(id)
                .name("Тестовый пользователь")
                .email("test123@email.ru")
                .build();
    }
}
