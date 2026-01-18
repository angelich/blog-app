package ru.angelich.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.angelich.comments.models.Comment;
import ru.angelich.comments.models.CommentRequest;
import ru.angelich.comments.models.CommentResponse;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    Comment toComment(CommentRequest commentRequest);

    CommentResponse toCommentResponse(Comment comment);
}
