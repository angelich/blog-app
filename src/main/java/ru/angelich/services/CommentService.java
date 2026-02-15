package ru.angelich.services;

import ru.angelich.models.comment.Comment;
import ru.angelich.models.comment.CommentRequest;
import ru.angelich.models.comment.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findAllByPostId(Long postId);

    CommentResponse findById(Long postId, Long commentId);

    CommentResponse createComment(Long postId, CommentRequest commentRequest);

    CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest);

    void deleteComment(Long postId, Long commentId);

    Comment findCommentOrThrow(Long postId, Long commentId);
}
