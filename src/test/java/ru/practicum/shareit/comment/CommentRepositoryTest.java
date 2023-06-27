package ru.practicum.shareit.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Проверка метода поиска всех комментариев по ID вещи")
    void check_findAllByItemId_shouldReturnCommentListOfItemWithSomeId() {
        User user1 = TestData.createTestUser(1L);
        User user2 = TestData.createTestUser(2L);
        user2.setEmail("test2@test.ru");
        User user3 = TestData.createTestUser(3L);
        user3.setEmail("test3@test.ru");

        Item item = TestData.createTestItem(1L, true, user1);
        Comment comment1 = new Comment(1L, "Комментарий", item, user2, LocalDateTime.now());
        Comment comment2 = new Comment(2L, "Комментарий", item, user3, LocalDateTime.now());

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        itemRepository.save(item);

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        List<Comment> expectedList = List.of(comment1, comment2);
        List<Comment> actualList = commentRepository.findAllByItemId(item.getId());

        assertThat(expectedList).isEqualTo(actualList);
        assertThat(expectedList.size()).isEqualTo(actualList.size());
    }
}
