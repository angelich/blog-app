package ru.angelich.post;

import lombok.Data;

import java.util.List;

@Data
public class Post {
    Long id;
    String title;
    String description;
    List<String> tags;
    Long likesCount;
    Long commentsCount;
}
