package ru.angelich.comments.models;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private String text;
    private Long postId;
}
