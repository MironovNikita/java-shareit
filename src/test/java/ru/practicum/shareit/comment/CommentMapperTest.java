package ru.practicum.shareit.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.TestData;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {
    @Spy
    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    @DisplayName("Проверка маппинга бронирования в BookingDatesDto")
    void check_transformCommentToCommentDto_shouldBeCorrectTransform() {
        User user = TestData.createTestUser(1L);
        Item item = TestData.createTestItem(1L, true, user);
        Comment comment = new Comment(1L, "Комментарий", item, user, LocalDateTime.now());

        CommentDto commentDto = commentMapper.transformCommentToCommentDto(comment);

        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }
}
