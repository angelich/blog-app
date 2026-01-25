package ru.angelich.models.post;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "text", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likesCount", constant = "0L")
    @Mapping(target = "commentsCount", constant = "0L")
    Post toPost(PostRequest postRequest);

    @Mapping(source = "description", target = "text")
    PostResponse toPostResponse(Post post);
}
