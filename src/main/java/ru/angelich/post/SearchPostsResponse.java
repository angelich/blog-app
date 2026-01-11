package ru.angelich.post;

import java.util.List;

public record SearchPostsResponse(List<Post> posts, Boolean hasPrev, Boolean hasNext, Integer lastPage) {
}
