package ru.angelich.post;

import lombok.Data;

@Data
public class CommentRequest {
    private String text;
    private Long postId;
}
