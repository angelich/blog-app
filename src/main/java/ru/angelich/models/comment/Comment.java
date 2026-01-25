package ru.angelich.models.comment;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private String description;
    private Long postId;
}
