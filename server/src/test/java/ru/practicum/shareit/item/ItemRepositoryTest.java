package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void checkFindAllByOwnerIdOrderByIdAscShouldReturnItemsByOwnerId() {
        User user1 = TestData.createTestUser(1L);
        Item item1 = new Item(1L, "Test", "Description", true,
                user1, null, null, null, null);
        Item item2 = new Item(2L, "Name", "TesTing description", true,
                user1, null, null, null, null);

        User user2 = TestData.createTestUser(2L);
        user2.setEmail("testing@test.ru");
        Item item3 = new Item(3L, "Name", "Description", true,
                user2, null, null, null, null);

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<Item> actualList = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId(), null);
        List<Item> expectedList = List.of(item1, item2);

        assertThat(actualList.size()).isEqualTo(2);
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("Проверка поиска вещей по тексту/части текста")
    void checkFindAllByTextShouldReturnAllItemsByNameOrDescription() {
        User user = TestData.createTestUser(1L);
        userRepository.save(user);

        Item item1 = new Item(1L, "Test", "Description", true,
                user, null, null, null, null);
        Item item2 = new Item(2L, "Name", "TesTing description", true,
                user, null, null, null, null);
        Item item3 = new Item(3L, "Name", "Description", true,
                user, null, null, null, null);

        EntityManager entityManager = testEntityManager.getEntityManager();
        TypedQuery<Item> itemTypedQuery = entityManager.createQuery(
                "SELECT i FROM Item i " +
                        "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
                        "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))" +
                        "AND i.available IS TRUE", Item.class);

        assertThat(itemTypedQuery.setParameter(1, "tEsT").getResultList()).isEmpty();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        assertThat(itemRepository.findAllByText("tEsT", null)).hasSize(2);
    }

    @Test
    void checkFindAllByRequestIdShouldReturnAllItemsByRequestId() {
        User owner = TestData.createTestUser(1L);
        User requestor = TestData.createTestUser(2L);
        owner.setEmail("testing@test.ru");
        long requestId = 1L;
        Request request = TestData.createTestRequest(requestId, LocalDateTime.now(), requestor);

        userRepository.save(owner);
        userRepository.save(requestor);
        requestRepository.save(request);

        Item item1 = new Item(1L, "Test", "Description", true,
                owner, null, null, null, null);
        Item item2 = new Item(2L, "Name", "TesTing description", true,
                owner, request, null, null, null);
        Item item3 = new Item(3L, "Name", "Description", true,
                owner, request, null, null, null);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<Item> requesting = itemRepository.findAllByRequestId(request.getId());
        List<Item> expectedList = List.of(item2, item3);

        assertThat(requesting.size()).isEqualTo(2);
        assertThat(requesting).isEqualTo(expectedList);
    }
}