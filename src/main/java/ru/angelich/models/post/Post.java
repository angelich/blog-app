package ru.angelich.models.post;

import lombok.Data;

import java.util.List;

@Data
public class Post {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;
}
