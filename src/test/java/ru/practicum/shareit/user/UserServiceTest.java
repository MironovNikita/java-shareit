package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.common.exception.DuplicateEmailException;
import ru.practicum.shareit.common.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Проверка метода обновления пользователя (имени)")
    void checkUpdateShouldUpdateUserName() {
        long id = 1L;
        String name = "Новое имя";

        UserDto userDto = new UserDto(name, null);
        User user = TestData.createTestUser(id);
        User newUser = TestData.createTestUser(id);
        newUser.setName(name);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThat(userService.update(id, userDto)).isEqualTo(newUser);
        assertThat(user.getName()).isEqualTo(userDto.getName());
    }

    @Test
    @DisplayName("Проверка метода обновления пользователя (email)")
    void checkUpdateShouldUpdateUserEmail() {
        long id = 1L;
        String email = "newemail@email.ru";

        UserDto userDto = new UserDto(null, email);
        User user = TestData.createTestUser(id);
        User newUser = TestData.createTestUser(id);
        newUser.setEmail(email);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThat(userService.update(id, userDto)).isEqualTo(newUser);
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    @DisplayName("Проверка метода обновления пользователя при дублировании email")
    void checkUpdateShouldThrowDuplicateEmailExceptionIfEmailExists() {
        long id = 1L;
        String email = "test1234@email.ru";

        UserDto userDto = new UserDto(null, email);
        User user = TestData.createTestUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when((userRepository.findUserByEmail(email))).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.update(id, userDto)).isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    @DisplayName("Проверка метода обновления пользователя при ненайденном пользователе")
    void checkUpdateShouldThrowObjectNotFoundExceptionIfUserNotFound() {
        UserDto userDto = new UserDto("Какое-то имя", "email@email.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(1L, userDto)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    @DisplayName("Проверка метода получения пользователя по ID")
    void checkGetShouldReturnUserById() {
        long id = 1L;
        User user = TestData.createTestUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThat(userService.get(id)).isEqualTo(user);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Проверка метода получения пользователя по несуществующему ID")
    void checkGetShouldThrowObjectNotFoundException() {
        long id = 9999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.get(id)).isInstanceOf(ObjectNotFoundException.class);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Проверка метода получения списка всех пользователей")
    void checkGetAllShouldReturnAllUserList() {
        List<User> expectedList = List.of(TestData.createTestUser(1L),
                TestData.createTestUser(2L),
                TestData.createTestUser(3L));

        when(userRepository.findAll()).thenReturn(expectedList);

        assertThat(userService.getAll()).isEqualTo(expectedList);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Проверка метода получения списка всех пользователей при пустом списке")
    void checkGetAllShouldReturnEmptyUserList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThat(userService.getAll()).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Проверка метода удаления пользователя по ID")
    void checkDeleteShouldDeleteUser() {
        long id = 1L;
        User user = TestData.createTestUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.delete(id);

        assertThat(userService.getAll()).isEmpty();
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Проверка метода удаления пользователя по несуществующему ID")
    void checkDeleteShouldThrowObjectNotFoundExceptionIfNonexistentId() {
        long id = 9999L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(id)).isInstanceOf(ObjectNotFoundException.class);
    }
}
