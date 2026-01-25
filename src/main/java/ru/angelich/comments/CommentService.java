package ru.angelich.comments;

import ru.angelich.comments.models.Comment;
import ru.angelich.comments.models.CommentRequest;
import ru.angelich.comments.models.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findAllByPostId(Long postId);

    CommentResponse findById(Long postId, Long commentId);

    CommentResponse createComment(Long postId, CommentRequest commentRequest);

    CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest);

    void deleteComment(Long postId, Long commentId);

    Comment findCommentOrThrow(Long postId, Long commentId);
}
