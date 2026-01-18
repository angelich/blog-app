package ru.angelich.comments.models;

import lombok.Data;

@Data
public class CommentRequest {
    private String text;
    private Long postId;
}
