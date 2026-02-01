package ru.angelich.models.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;
    private Long likesCount;
    private Long commentsCount;
}
