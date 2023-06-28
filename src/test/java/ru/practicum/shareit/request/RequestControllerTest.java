package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private RequestController requestController;

    @Test
    @DisplayName("Проверка метода создания запроса")
    void check_create_shouldReturnCreatedRequest() throws Exception {
        long userId = 1L;
        RequestDto requestDto = RequestDto.builder()
                .description("Описание запроса")
                .build();
        RequestDto fullRequestDto = TestData.createTestRequestDto(1L);

        when(requestService.create(userId, requestDto)).thenReturn(fullRequestDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(fullRequestDto)));
        verify(requestService, times(1)).create(userId, requestDto);
    }

    @Test
    @DisplayName("Проверка метода получения собственных запросов")
    void check_getOwnRequests_shouldReturnOwnUserRequests() throws Exception {
        long userId = 1L;
        List<RequestDto> expectedList = List.of(
                TestData.createTestRequestDto(1L),
                TestData.createTestRequestDto(2L),
                TestData.createTestRequestDto(3L));

        when(requestService.getOwnRequests(userId)).thenReturn(expectedList);

        mockMvc.perform(get("/requests").header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
        verify(requestService, times(1)).getOwnRequests(userId);
    }

    @Test
    @DisplayName("Проверка метода получения запросов других пользователей")
    void check_getOtherUsersRequests_shouldReturnOtherUsersRequests() throws Exception {
        long userId = 1L;
        List<RequestDto> expectedList = List.of(
                TestData.createTestRequestDto(1L),
                TestData.createTestRequestDto(2L),
                TestData.createTestRequestDto(3L));

        when(requestService.getOtherUsersRequests(anyLong(), any())).thenReturn(expectedList);

        mockMvc.perform(get("/requests/all").header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
        verify(requestService, times(1)).getOtherUsersRequests(anyLong(), any());
    }

    @Test
    @DisplayName("Проверка метода получения запроса")
    void check_get_shouldReturnRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        RequestDto requestDto = TestData.createTestRequestDto(requestId);

        when(requestService.get(userId, requestId)).thenReturn(requestDto);

        mockMvc.perform(get(String.format("/requests/%d", requestId)).header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
        verify(requestService, times(1)).get(userId, requestId);
    }

    @Test
    @DisplayName("Проверка метода получения запроса по несуществующему ID")
    void check_get_shouldThrowObjectNotFoundExceptionIfNonexistentRequestId() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        when(requestService.get(userId, requestId)).thenThrow(new ObjectNotFoundException("Запрос", requestId));

        mockMvc.perform(get(String.format("/requests/%d", requestId)).header(HEADER_USER_ID, userId))
                .andExpect(status().isNotFound());
        verify(requestService, times(1)).get(userId, requestId);
    }
}
