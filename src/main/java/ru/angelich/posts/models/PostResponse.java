package ru.angelich.posts.models;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;
}
