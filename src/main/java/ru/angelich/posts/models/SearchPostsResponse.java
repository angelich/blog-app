package ru.angelich.posts.models;

import java.util.List;

public record SearchPostsResponse(List<PostResponse> posts, Boolean hasPrev, Boolean hasNext, Integer lastPage) {
}
