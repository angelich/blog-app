package ru.angelich.posts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.angelich.posts.models.Post;
import ru.angelich.posts.models.PostRequestDto;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "text", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likesCount", constant = "0L")
    @Mapping(target = "commentsCount", constant = "0L")
    Post toPost(PostRequestDto postRequestDto);
}
