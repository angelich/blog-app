package ru.angelich.comments;

import org.springframework.stereotype.Service;
import ru.angelich.comments.models.Comment;
import ru.angelich.comments.models.CommentRequest;
import ru.angelich.comments.models.CommentResponse;
import ru.angelich.posts.PostService;

import java.util.List;

@Service
public class CommentService {
    private final PostService postService;
    private final CommentRepository commentRepository;

    public CommentService(PostService postService, CommentRepository commentRepository) {
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    public List<CommentResponse> findAllByPostId(Long postId) {
        postService.getPostByIdOrThrow(postId);

        return commentRepository.findAllByPostId(postId)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentResponse)
                .toList();
    }

    public CommentResponse findById(Long postId, Long commentId) {
        var comment = findCommentOrThrow(postId, commentId);
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    public CommentResponse createComment(Long postId, CommentRequest commentRequest) {
        postService.getPostByIdOrThrow(postId);

        var comment = commentRepository.saveComment(postId, CommentMapper.INSTANCE.toComment(commentRequest));
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest) {
        findById(postId, commentId);
        var comment = commentRepository.updateComment(postId, commentId, CommentMapper.INSTANCE.toComment(commentRequest));
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    public void deleteComment(Long postId, Long commentId) {
        findById(postId, commentId);
        commentRepository.deleteComment(postId, commentId);
    }

    public Comment findCommentOrThrow(Long postId, Long commentId) {
        return commentRepository.findById(postId, commentId).orElseThrow(
                () -> new IllegalArgumentException("Comment not found"));
    }
}
