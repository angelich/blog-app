package ru.angelich.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.angelich.UnitTestConfig;
import ru.angelich.errors.CommentNotFoundException;
import ru.angelich.models.comment.Comment;
import ru.angelich.models.comment.CommentRequest;
import ru.angelich.models.comment.CommentResponse;
import ru.angelich.models.post.Post;
import ru.angelich.repositories.CommentRepository;
import ru.angelich.repositories.PostRepository;
import ru.angelich.services.CommentService;
import ru.angelich.services.PostService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = UnitTestConfig.class)
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        reset(commentRepository);
    }

    @Test
    void findAllByPostId_success() {
        Long postId = 1L;
        var post = new Post(postId, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        Comment comment1 = new Comment(1L, "Content 1", postId);
        Comment comment2 = new Comment(2L, "Content 2", postId);

        CommentResponse response1 = new CommentResponse(1L, "Content 1", postId);
        CommentResponse response2 = new CommentResponse(2L, "Content 2", postId);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment1, comment2));

        List<CommentResponse> result = commentService.findAllByPostId(postId);

        assertEquals(2, result.size());
        assertEquals(response1, result.getFirst());
        assertEquals(response2, result.getLast());

        verify(commentRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    void createComment_success() {
        Long postId = 1L;
        var post = new Post(postId, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        var commentRequest = new CommentRequest("Comment", 1L);
        var newComment = new Comment(1L, "Comment", 1L);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(commentRepository.saveComment(any(), any())).thenReturn(newComment);

        var commentResponse = commentService.createComment(1L, commentRequest);

        assertEquals(1L, commentResponse.id());
        assertEquals("Comment", commentResponse.text());
        assertEquals(1L, commentResponse.postId());
    }

    @Test
    void updateComment_success() {
        var commentRequest = new CommentRequest("Comment", 1L);
        var newComment = new Comment(1L, "Comment", 1L);

        when(commentRepository.findById(any(), any())).thenReturn(Optional.of(newComment));
        when(commentRepository.updateComment(any(), any(), any())).thenReturn(newComment);

        var commentResponse = commentService.updateComment(1L, 1L, commentRequest);

        assertEquals(1L, commentResponse.id());
        assertEquals("Comment", commentResponse.text());
        assertEquals(1L, commentResponse.postId());
    }

    @Test
    void updateComment_commentNotFound() {
        var commentRequest = new CommentRequest("Comment", 1L);

        when(commentRepository.findById(any(), any())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.updateComment(1L, 1L, commentRequest));
    }

    @Test
    void deleteComment_success() {
        var comment = new Comment(1L, "Comment", 1L);
        when(commentRepository.findById(any(), any())).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentService.deleteComment(1L, 1L));
    }
}