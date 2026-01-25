package ru.angelich.models.post;

import java.util.List;

public record PostResponse(Long id, String title, String text, List<String> tags, Long likesCount,
                           Long commentsCount) {
}
