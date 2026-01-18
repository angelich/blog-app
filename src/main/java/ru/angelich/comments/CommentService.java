package ru.angelich.comments;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import ru.angelich.comments.models.CommentRequest;
import ru.angelich.comments.models.CommentResponse;
import ru.angelich.posts.PostRepository;

import java.util.List;

@Service
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public @NonNull List<CommentResponse> findAllByPostId(Long postId) {
        postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("Post not found")); // TODO: убрать дублирование кода

        return commentRepository.findAllByPostId(postId)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentResponse)
                .toList();
    }

    public @NonNull CommentResponse findById(Long postId, Long commentId) {
        postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));

        var comment = commentRepository.findById(postId, commentId).orElseThrow(
                () -> new IllegalArgumentException("Comment not found"));

        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    public @NonNull CommentResponse createComment(Long postId, CommentRequest commentRequest) {
        postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));

        var comment = commentRepository.saveComment(postId, CommentMapper.INSTANCE.toComment(commentRequest));
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    public @NonNull CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest) {
        findById(postId, commentId);
        var comment = commentRepository.updateComment(postId, commentId, CommentMapper.INSTANCE.toComment(commentRequest));
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    public void deleteComment(Long postId, Long commentId) {
        findById(postId, commentId);
        commentRepository.deleteComment(postId, commentId);
    }
}
