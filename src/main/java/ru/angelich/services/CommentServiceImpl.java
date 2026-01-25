package ru.angelich.services;

import org.springframework.stereotype.Service;
import ru.angelich.models.comment.Comment;
import ru.angelich.models.comment.CommentMapper;
import ru.angelich.models.comment.CommentRequest;
import ru.angelich.models.comment.CommentResponse;
import ru.angelich.repositories.CommentRepository;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final PostService postService;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(PostService postService, CommentRepository commentRepository) {
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<CommentResponse> findAllByPostId(Long postId) {
        postService.getPostByIdOrThrow(postId);

        return commentRepository.findAllByPostId(postId)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentResponse)
                .toList();
    }

    @Override
    public CommentResponse findById(Long postId, Long commentId) {
        var comment = findCommentOrThrow(postId, commentId);
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    @Override
    public CommentResponse createComment(Long postId, CommentRequest commentRequest) {
        postService.getPostByIdOrThrow(postId);

        var comment = commentRepository.saveComment(postId, CommentMapper.INSTANCE.toComment(commentRequest));
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    @Override
    public CommentResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest) {
        findById(postId, commentId);
        var comment = commentRepository.updateComment(postId, commentId, CommentMapper.INSTANCE.toComment(commentRequest));
        return CommentMapper.INSTANCE.toCommentResponse(comment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        findById(postId, commentId);
        commentRepository.deleteComment(postId, commentId);
    }

    @Override
    public Comment findCommentOrThrow(Long postId, Long commentId) {
        return commentRepository.findById(postId, commentId).orElseThrow(
                () -> new IllegalArgumentException("Comment not found"));
    }
}
