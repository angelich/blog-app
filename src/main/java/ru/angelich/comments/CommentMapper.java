package ru.angelich.comments;

import org.jspecify.annotations.NonNull;
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
    @NonNull
    Comment toComment(CommentRequest commentRequest);

    @NonNull
    CommentResponse toCommentResponse(Comment comment);
}
