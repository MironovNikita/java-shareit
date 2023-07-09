package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestData;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void addUsers() {
        userRepository.save(TestData.createTestUser(1L));
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Поиск пользователя по email")
    void checkFindUserByEmailShouldReturnRightUserByEmail() {
        String name = "Тестовый пользователь";
        String email = "test123@email.ru";
        User expectedUser = new User(1L, name, email);

        Optional<User> actualUser = userRepository.findUserByEmail(email);

        assertTrue(actualUser.isPresent());
        assertThat(expectedUser.getName()).isEqualTo(actualUser.get().getName());
        assertThat(expectedUser.getEmail()).isEqualTo((actualUser.get().getEmail()));
        assertThat(expectedUser.getId()).isEqualTo(actualUser.get().getId());
    }
}