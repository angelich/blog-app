package ru.angelich.posts.models;

import java.util.List;

public record PostResponse(Long id, String title, String description, List<String> tags, Long likesCount,
                           Long commentsCount) {
}
