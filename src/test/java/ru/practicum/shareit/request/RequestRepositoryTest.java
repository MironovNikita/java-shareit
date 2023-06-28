package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.common.pagination.Pagination;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestRepositoryTest {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Проверка метода получения собственных запросов")
    void check_findAllByUserIdOrderByCreatedDesc_shouldReturnOwnRequestList() {
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        userRepository.save(user);

        LocalDateTime checkTime = LocalDateTime.now();

        Request request1 = TestData.createTestRequest(1L, checkTime.minusDays(1), user);
        Request request2 = TestData.createTestRequest(2L, checkTime.minusDays(2), user);
        Request request3 = TestData.createTestRequest(3L, checkTime.minusDays(3), user);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);

        List<Request> expectedList = requestRepository.findAllByUserIdOrderByCreatedDesc(userId);

        assertThat(expectedList.size()).isEqualTo(3);
        assertThat(expectedList.get(0)).isEqualTo(request1);
        assertThat(expectedList.get(1)).isEqualTo(request2);
        assertThat(expectedList.get(2)).isEqualTo(request3);
    }

    @Test
    @DisplayName("Проверка метода получения собственных запросов с корректными датами создания")
    void check_findAllByUserIdOrderByCreatedDesc_shouldReturnOwnRequestListWithCorrectCreationDate() {
        long userId = 1L;
        User user = TestData.createTestUser(userId);
        userRepository.save(user);

        LocalDateTime checkTime = LocalDateTime.now();

        Request request1 = TestData.createTestRequest(1L, checkTime.minusDays(1), user);
        Request request2 = TestData.createTestRequest(2L, checkTime.minusDays(2), user);
        Request request3 = TestData.createTestRequest(3L, checkTime.minusDays(3), user);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);

        List<Request> expectedList = requestRepository.findAllByUserIdOrderByCreatedDesc(userId);

        assertThat(expectedList.get(0).getCreated()).isAfter(expectedList.get(1).getCreated());
        assertThat(expectedList.get(1).getCreated()).isAfter(expectedList.get(2).getCreated());
    }

    @Test
    @DisplayName("Проверка метода получения запросов других пользователей")
    void check_findAllByUserIdIsNotOrderByCreatedDesc_shouldReturnOtherUsersRequestList() {
        long userId = 1L;
        User requester = TestData.createTestUser(userId);
        User user1 = TestData.createTestUser(2L);
        user1.setEmail("test1@test.ru");
        User user2 = TestData.createTestUser(3L);
        user2.setEmail("test2@test.ru");

        userRepository.save(requester);
        userRepository.save(user1);
        userRepository.save(user2);

        LocalDateTime checkTime = LocalDateTime.now();

        Request request1 = TestData.createTestRequest(1L, checkTime.minusDays(1), user1);
        Request request2 = TestData.createTestRequest(2L, checkTime.minusDays(2), user2);
        Request request3 = TestData.createTestRequest(3L, checkTime.minusDays(3), user1);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);

        List<Request> expectedList = requestRepository.findAllByUserIdIsNotOrderByCreatedDesc(userId,
                Pagination.splitByPages(0, 20));
        assertThat(expectedList.size()).isEqualTo(3);
        assertThat(expectedList.get(0)).isEqualTo(request1);
        assertThat(expectedList.get(1)).isEqualTo(request2);
        assertThat(expectedList.get(2)).isEqualTo(request3);
    }
}
