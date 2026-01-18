package ru.angelich.post;

import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @NonNull
    Comment toComment(CommentRequest commentRequest);

    @NonNull
    CommentResponse toCommentResponse(Comment comment);
}
