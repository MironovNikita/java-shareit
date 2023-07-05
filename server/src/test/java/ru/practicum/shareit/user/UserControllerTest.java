package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Проверка метода на создание пользователя")
    void checkCreateShouldCreateUser() throws Exception {
        User user = TestData.createTestUser(1);
        UserDto userDto = new UserDto(user.getName(), user.getEmail());

        when(userService.create(userDto)).thenReturn(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    @DisplayName("Проверка метода на обновление пользователя")
    void checkUpdateShouldUpdateUser() throws Exception {
        long id = 1L;
        User user = TestData.createTestUser(id);
        UserDto userDto = new UserDto(user.getName(), user.getEmail());

        when(userService.update(id, userDto)).thenReturn(user);

        mockMvc.perform(patch(String.format("/users/%d", id)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json((objectMapper.writeValueAsString(user))));
        verify(userService, times(1)).update(id, userDto);
    }

    @Test
    @DisplayName("Проверка метода на обновление несуществующего пользователя")
    void checkUpdateShouldReturnNotFoundStatusIfUpdatingNonexistentUser() throws Exception {
        long id = 1L;
        User user = TestData.createTestUser(id);
        UserDto userDto = new UserDto(user.getName(), user.getEmail());

        when(userService.update(id, userDto)).thenThrow(new ObjectNotFoundException("Пользователь", id));

        mockMvc.perform(patch(String.format("/users/%d", id)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
        verify(userService, times(1)).update(id, userDto);
    }

    @Test
    @DisplayName("Проверка метода на получение пользователя по ID")
    void checkGetShouldReturnUserById() throws Exception {
        long id = 1L;
        User user = TestData.createTestUser(id);

        when(userService.get(id)).thenReturn(user);

        mockMvc.perform(get(String.format("/users/%d", id)).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
        verify(userService, times(1)).get(id);
    }

    @Test
    @DisplayName("Проверка метода на получение пользователя по несуществующему ID")
    void checkGetShouldReturnObjectNotFoundExceptionStatusIfNonexistentId() throws Exception {
        long id = 9999L;

        when(userService.get(id)).thenThrow(new ObjectNotFoundException("Пользователь", id));

        mockMvc.perform(get(String.format("/users/%d", id))).andExpect(status().isNotFound());
        verify(userService, times(1)).get(id);
    }

    @Test
    @DisplayName("Проверка метода на получение пользователя по некорректному ID")
    void checkGetShouldReturnInternalServerErrorStatusIfIncorrectId() throws Exception {
        mockMvc.perform(get("/users/sometext75")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка метода на получение списка всех пользователей")
    void checkGetAllShouldReturnAllUserList() throws Exception {
        List<User> expectedList = List.of(TestData.createTestUser(1L),
                                          TestData.createTestUser(2L),
                                          TestData.createTestUser(3L));

        when(userService.getAll()).thenReturn(expectedList);

        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
        verify(userService, times(2)).getAll();
    }

    @Test
    @DisplayName("Проверка метода на получение списка всех пользователей, когда список пуст")
    void checkGetAllShouldReturnEmptyUserList() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
        verify(userService, times(2)).getAll();
    }

    @Test
    @DisplayName("Проверка метода на удаление пользователя по ID")
    void checkDeleteShouldDeleteUserById() throws Exception {
        long id = 1L;

        mockMvc.perform(delete(String.format("/users/%d", id))).andExpect(status().isOk());
        verify(userService, times(1)).delete(id);
    }

    @Test
    @DisplayName("Проверка метода на удаление пользователя по несуществующему ID")
    void checkDeleteShouldDeleteUserByNonexistentId() throws Exception {
        long id = 9999L;

        doThrow(new ObjectNotFoundException("Пользователь", id)).when(userService).delete(id);

        mockMvc.perform(delete(String.format("/users/%d", id))).andExpect(status().isNotFound());
        verify(userService, times(1)).delete(id);
    }
}