package ru.angelich.models.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "text")
    Comment toComment(CommentRequest commentRequest);

    @Mapping(target = "text", source = "description")
    CommentResponse toCommentResponse(Comment comment);
}
