package ru.practicum.shareit.storageTest;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepositoryImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class UserRepositoryTest {
    private final InMemoryUserRepositoryImpl userRepository = new InMemoryUserRepositoryImpl();

    @AfterEach
    void resetData() {
        userRepository.resetData();
    }

    @DisplayName("Проверка метода создания пользователя")
    @Test
    void shouldCreateUser() {
        User user1 = createTestUser();
        userRepository.create(user1);
        System.out.println(userRepository.getAll());
        Assertions.assertEquals(user1, userRepository.get(1).get());

        User user2 = createTestUser().withName("User").withEmail("test@mail.ru");
        userRepository.create(user2);
        Assertions.assertEquals(user2, userRepository.get(2).get());

        assertThat(userRepository.getAll().size()).isEqualTo(2);
    }

    @DisplayName("Проверка метода обновления пользователя")
    @Test
    void shouldUpdateUser() {
        User user = createTestUser();
        User userAsUpdate = createTestUser().withId(1L).withName("John").withEmail("test@mail.ru");
        userRepository.create(user);

        userRepository.update(userAsUpdate);

        assertThat(userRepository.get(1).get()).isEqualTo(userAsUpdate);
    }

    @DisplayName("Проверка метода обновления пользователя при передаче несуществующего id")
    @Test
    void shouldNotUpdateUserIfIdDoesNotExist() {
        User user = createTestUser();
        User userAsUpdate = createTestUser().withId(12L).withName("Name2").withEmail("test2@yandex.ru");
        userRepository.create(user);

        assertThat(userRepository.update(userAsUpdate)).isNotEqualTo(userRepository.get(1));
        assertThat(userRepository.get(1).get()).isEqualTo(user);
        assertThat(userRepository.get(1).get()).isNotEqualTo(userAsUpdate);
    }

    @DisplayName("Проверка метода получения пользователя по id")
    @Test
    void shouldReturnUserById() {
        User user1 = createTestUser();
        User user2 = createTestUser().withName("Name2").withEmail("test2@yandex.ru");
        User user3 = createTestUser().withName("Name3").withEmail("test3@yandex.ru");

        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);

        assertThat(userRepository.get(1).get()).isEqualTo(user1);
        assertThat(userRepository.get(2).get()).isEqualTo(user2);
        assertThat(userRepository.get(3).get()).isEqualTo(user3);
    }

    @DisplayName("Проверка метода получения пользователя по несуществующему id")
    @Test
    void shouldReturnUserByIdIfIdDoesNotExist() {
        User user1 = createTestUser();
        User user2 = createTestUser().withName("Name2").withEmail("test2@yandex.ru");

        userRepository.create(user1);
        userRepository.create(user2);

        assertThat(userRepository.getAll().size()).isEqualTo(2);
        assertThat(userRepository.get(3000)).isEmpty();
    }

    @DisplayName("Проверка метода получения пользователя по email")
    @Test
    void shouldReturnUserByEmail() {
        User user = createTestUser();
        userRepository.create(user);

        assertThat(userRepository.get("test@yandex.ru").get()).isEqualTo(user);
    }

    @DisplayName("Проверка метода получения пользователя по несуществующему email")
    @Test
    void shouldReturnUserByNonExistentEmail() {
        User user = createTestUser();
        userRepository.create(user);

        assertThat(userRepository.get("smth@yandex.ru")).isEmpty();
    }

    @DisplayName("Проверка метода получения всех пользователей")
    @Test
    void shouldReturnAllUsers() {
        User user1 = createTestUser();
        User user2 = createTestUser().withName("Name2").withEmail("test2@yandex.ru");
        User user3 = createTestUser().withName("Name3").withEmail("test3@yandex.ru");

        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);

        List<User> userCheckList = List.of(user1, user2, user3);
        Assertions.assertEquals(userCheckList, userRepository.getAll());
    }

    @DisplayName("Проверка метода получения всех пользователей при пустом списке")
    @Test
    void shouldNotReturnAllUsersBecauseOfTableUsersIsEmpty() {
        assertThat(userRepository.getAll().size()).isEqualTo(0);
    }

    @DisplayName("Проверка метода удаления пользователя по id")
    @Test
    void shouldDeleteUserById() {
        User user1 = createTestUser();
        User user2 = createTestUser().withName("Name2").withEmail("test2@yandex.ru");
        User user3 = createTestUser().withName("Name3").withEmail("test3@yandex.ru");

        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);

        assertThat(userRepository.getAll().size()).isEqualTo(3);
        assertThat(userRepository.delete(1)).isEqualTo(true);
        assertThat(userRepository.getAll().size()).isEqualTo(2);
    }

    @DisplayName("Проверка метода удаления пользователя по несуществующему id")
    @Test
    void shouldNotDeleteAnyUserByNonExistentId() {
        User user1 = createTestUser();
        User user2 = createTestUser().withName("Name2").withEmail("test2@yandex.ru");

        userRepository.create(user1);
        userRepository.create(user2);

        assertThat(userRepository.getAll().size()).isEqualTo(2);
        assertThat(userRepository.delete(213)).isEqualTo(false);
        assertThat(userRepository.getAll().size()).isEqualTo(2);
    }

    private User createTestUser() {
        return User.builder()
                .name("Nikita")
                .email("test@yandex.ru")
                .build();
    }
}
