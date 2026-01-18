package ru.angelich.comments.models;

public record CommentRequest(String text, Long postId) {
}
