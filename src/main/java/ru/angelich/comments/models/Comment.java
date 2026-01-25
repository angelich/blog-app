package ru.angelich.comments.models;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private String description;
    private Long postId;
}
