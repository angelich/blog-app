package ru.angelich.models.post;

import java.util.List;

public record SearchPostsResponse(List<PostResponse> posts, Boolean hasPrev, Boolean hasNext, Integer lastPage) {
}
