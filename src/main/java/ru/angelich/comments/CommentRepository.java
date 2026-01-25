package ru.angelich.comments;

import ru.angelich.comments.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findById(Long postId, Long commentId);

    Comment saveComment(Long postId, Comment comment);

    Comment updateComment(Long postId, Long commentId, Comment comment);

    void deleteComment(Long postId, Long commentId);
}
