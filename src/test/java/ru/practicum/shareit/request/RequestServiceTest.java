package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
    @Mock
    private UserService userService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    @DisplayName("Проверка метода создания запроса")
    void checkCreateShouldReturnCreatedRequest() {
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        RequestDto requestDto = RequestDto.builder()
                .description("Описание запроса")
                .build();

        when(userService.get(userId)).thenReturn(user);
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RequestDto fullRequestDto = requestService.create(userId, requestDto);

        assertNull(fullRequestDto.getItems());
        assertThat(fullRequestDto.getDescription()).isEqualTo(requestDto.getDescription());
    }

    @Test
    @DisplayName("Проверка метода создания запроса при несуществующем пользователе")
    void checkCreateShouldThrowObjectNotFoundExceptionIfNonexistentUserId() {
        long userId = 1L;
        RequestDto requestDto = RequestDto.builder()
                .description("Описание запроса")
                .build();

        when(userService.get(userId)).thenThrow(new ObjectNotFoundException("Пользователь", userId));

        assertThatThrownBy(() -> {
            requestService.create(userId, requestDto);
        }).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения собственных запросов")
    void checkGetOwnRequestsShouldReturnOwnRequestList() {
        long userId = 1L;
        User user = TestData.createTestUser(userId);

        List<Request> expectedList = List.of(
                TestData.createTestRequest(1L, LocalDateTime.now(), user),
                TestData.createTestRequest(2L, LocalDateTime.now(), user),
                TestData.createTestRequest(3L, LocalDateTime.now(), user));

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findAllByUserIdOrderByCreatedDesc(userId)).thenReturn(expectedList);

        assertThat(requestService.getOwnRequests(userId)).isEqualTo(
                expectedList.stream().map(requestMapper::transformRequestToRequestDto)
                        .peek(requestDto -> requestDto.setItems(Collections.emptyList())).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Проверка метода получения собственных запросов по несуществующему ID")
    void checkGetOwnRequestsShouldThrowObjectNotFoundExceptionIfNonexistentUserId() {
        long userId = 1L;

        when(userService.get(userId)).thenThrow(new ObjectNotFoundException("Пользователь", userId));

        assertThatThrownBy(() -> {
            requestService.getOwnRequests(userId);
        }).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения запросов других пользователей")
    void checkGetOtherUsersRequestsShouldReturnOtherUsersRequestList() {
        long userId = 1L;
        User user = TestData.createTestUser(userId);

        List<Request> expectedList = List.of(
                TestData.createTestRequest(1L, LocalDateTime.now(), user),
                TestData.createTestRequest(2L, LocalDateTime.now(), user),
                TestData.createTestRequest(3L, LocalDateTime.now(), user));

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findAllByUserIdIsNotOrderByCreatedDesc(userId, null)).thenReturn(expectedList);

        assertThat(requestService.getOtherUsersRequests(userId, null)).isEqualTo(
                expectedList.stream().map(requestMapper::transformRequestToRequestDto)
                        .peek(requestDto -> requestDto.setItems(Collections.emptyList())).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Проверка метода получения запросов других пользователей по несуществующему ID")
    void checkGetOtherUsersRequestsShouldThrowObjectNotFoundExceptionIfNonexistentUserId() {
        long userId = 1L;

        when(userService.get(userId)).thenThrow(new ObjectNotFoundException("Пользователь", userId));

        assertThatThrownBy(() -> requestService.getOtherUsersRequests(userId, null))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения запроса по ID")
    void checkGetShouldReturnRequestById() {
        long requestId = 1L;
        long userId = 1L;

        User user = TestData.createTestUser(userId);
        Request request = TestData.createTestRequest(requestId, LocalDateTime.now(), user);
        RequestDto requestDto = requestMapper.transformRequestToRequestDto(request);
        requestDto.setItems(Collections.emptyList());

        when(userService.get(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Collections.emptyList());
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThat(requestService.get(userId, requestId)).isEqualTo(requestDto);
    }

    @Test
    @DisplayName("Проверка метода получения запроса по несуществующему ID")
    void checkGetShouldThrowObjectNotFoundExceptionIfNonExistentRequestId() {
        long requestId = 1L;
        long userId = 1L;

        when(requestRepository.findById(requestId)).thenThrow(new ObjectNotFoundException("Запрос", requestId));

        assertThatThrownBy(() -> requestService.get(userId, requestId)).isInstanceOf(ObjectNotFoundException.class);
    }
}
