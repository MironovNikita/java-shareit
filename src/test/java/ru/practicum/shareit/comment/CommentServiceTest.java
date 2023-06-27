package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @InjectMocks
    private CommentServiceImpl commentService;

    @SneakyThrows
    @Test
    @DisplayName("Проверка метода создания комментария")
    void check_createComment_shouldCreateComment() {
        User user = TestData.createTestUser(1L);
        Item item = TestData.createTestItem(1L, true, user);
        Comment comment = new Comment(1L, "Комментарий", item, user, LocalDateTime.now());

        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Comment checkedComment = commentService.createComment(comment);

        assertThat(checkedComment).isEqualTo(comment);
    }

    @SneakyThrows
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
        List<Comment> expectedList = List.of(comment1, comment2);

        when(commentRepository.findAllByItemId(anyLong())).thenReturn(expectedList);

        List<Comment> actualList = commentService.findAllByItemId(anyLong());

        assertThat(expectedList).isEqualTo(actualList);
        assertThat(expectedList.size()).isEqualTo(actualList.size());
    }
}
