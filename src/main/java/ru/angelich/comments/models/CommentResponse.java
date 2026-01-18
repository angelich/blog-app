package ru.angelich.comments.models;

import lombok.Data;

@Data
public class CommentResponse {
    private Long id;
    private String text;
    private Long postId;
}
