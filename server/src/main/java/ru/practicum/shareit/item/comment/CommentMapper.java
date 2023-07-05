package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment transformCommentDtoToComment(CommentDto commentDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto transformCommentToCommentDto(Comment comment);
}
