package ru.practicum.shareit.storageTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepositoryImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepositoryImpl;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ItemRepositoryTest {
    InMemoryItemRepositoryImpl itemRepository = new InMemoryItemRepositoryImpl();
    InMemoryUserRepositoryImpl userRepository = new InMemoryUserRepositoryImpl();

    @AfterEach
    void resetData() {
        itemRepository.resetData();
    }

    @DisplayName("Проверка метода создания предмета")
    @Test
    void shouldCreateItem() {
        Item item1 = createTestItem();
        itemRepository.create(item1);
        Assertions.assertEquals(item1, itemRepository.get(1).get());

        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        itemRepository.create(item2);
        Assertions.assertEquals(item2, itemRepository.get(2).get());

        assertThat(itemRepository.get(1).get()).isEqualTo(item1);
        assertThat(itemRepository.get(2).get()).isEqualTo(item2);
    }

    @DisplayName("Проверка метода обновления предмета")
    @Test
    void shouldUpdateItem() {
        Item item = createTestItem();
        Item itemAsUpdate = createTestItem();
        itemAsUpdate.setId(1L);
        itemAsUpdate.setName("Smth useful item");
        itemAsUpdate.setDescription("UseLess description");
        itemRepository.create(item);
        itemRepository.update(itemAsUpdate);

        assertThat(itemRepository.get(1).get()).isEqualTo(itemAsUpdate);
    }

    @DisplayName("Проверка метода обновления предмета при передаче несуществующего id")
    @Test
    void shouldNotUpdateItemIfIdDoesNotExist() {
        Item item = createTestItem();
        Item itemAsUpdate = createTestItem();
        itemAsUpdate.setId(12L);
        itemAsUpdate.setName("Smth useful item");
        itemAsUpdate.setDescription("UseLess description");

        itemRepository.create(item);

        assertThat(itemRepository.update(itemAsUpdate)).isNotEqualTo(userRepository.get(1));
        assertThat(itemRepository.get(1).get()).isEqualTo(item);
        assertThat(itemRepository.get(1).get()).isNotEqualTo(itemAsUpdate);
    }

    @DisplayName("Проверка метода получения предмета по id")
    @Test
    void shouldReturnItemById() {
        Item item1 = createTestItem();
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        assertThat(itemRepository.get(1).get()).isEqualTo(item1);
        assertThat(itemRepository.get(2).get()).isEqualTo(item2);
        assertThat(itemRepository.get(3).get()).isEqualTo(item3);
    }

    @DisplayName("Проверка метода получения предмета по несуществующему id")
    @Test
    void shouldNotReturnItemByIdIfIdDoesNotExist() {
        Item item1 = createTestItem();
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");

        itemRepository.create(item1);
        itemRepository.create(item2);

        assertThat(itemRepository.get(1).get()).isEqualTo(item1);
        assertThat(itemRepository.get(2).get()).isEqualTo(item2);
        assertThat(itemRepository.get(3000L)).isEmpty();
    }

    @DisplayName("Проверка метода получения предметов по id пользователя")
    @Test
    void shouldReturnItemListByUserId() {
        User user = createTestUser();
        userRepository.create(user);

        Item item1 = createTestItem();
        item1.setOwner(user);
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        item2.setOwner(user);
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");
        item3.setOwner(user);

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        List<Item> checkList = List.of(item1, item2, item3);

        assertThat(itemRepository.getByUserId(1)).isEqualTo(checkList);
    }

    @DisplayName("Проверка метода получения предметов по несуществующему id пользователя")
    @Test
    void shouldNotReturnItemListByUnexistingUserId() {
        User user = createTestUser();
        userRepository.create(user);

        Item item1 = createTestItem();
        item1.setOwner(user);
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        item2.setOwner(user);
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");
        item3.setOwner(user);

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        List<Item> checkList = List.of(item1, item2, item3);

        assertThat(itemRepository.getByUserId(2000).size()).isEqualTo(0);
    }

    @DisplayName("Проверка метода получения предметов по тексту")
    @Test
    void shouldReturnItemListByText() {
        User user = createTestUser();
        userRepository.create(user);

        Item item1 = createTestItem();
        item1.setOwner(user);
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        item2.setOwner(user);
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");
        item3.setOwner(user);

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        List<Item> checkList = List.of(item2, item3);

        assertThat(itemRepository.getBySearchText("useFul")).isEqualTo(checkList);
    }

    @DisplayName("Проверка метода получения предметов по пустому тексту")
    @Test
    void shouldReturnEmptyItemListByEmptyText() {
        User user = createTestUser();
        userRepository.create(user);

        Item item1 = createTestItem();
        item1.setOwner(user);
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        item2.setOwner(user);
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");
        item3.setOwner(user);

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        assertThat(itemRepository.getBySearchText("")).isEqualTo(Collections.emptyList());
    }

    @DisplayName("Проверка метода удаления предмета по id")
    @Test
    void shouldDeleteUserById() {
        Item item1 = createTestItem();
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        assertThat(itemRepository.delete(1)).isEqualTo(true);
    }

    @DisplayName("Проверка метода удаления предмета по несуществующему id")
    @Test
    void shouldNotDeleteUserByUnexistingId() {
        Item item1 = createTestItem();
        Item item2 = createTestItem();
        item2.setName("Smth useful item");
        item2.setDescription("UseLess description");
        Item item3 = createTestItem();
        item3.setName("Useful item");
        item3.setDescription("Description");

        itemRepository.create(item1);
        itemRepository.create(item2);
        itemRepository.create(item3);

        assertThat(itemRepository.delete(1000)).isEqualTo(false);
    }

    private Item createTestItem() {
        return Item.builder()
                .name("Item Test")
                .description("Test description")
                .available(true)
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .name("Nikita")
                .email("test@yandex.ru")
                .build();
    }
}
