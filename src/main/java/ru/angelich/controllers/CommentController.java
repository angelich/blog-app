package ru.angelich.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.angelich.models.comment.CommentRequest;
import ru.angelich.models.comment.CommentResponse;
import ru.angelich.models.post.PostRequest;
import ru.angelich.models.post.PostResponse;
import ru.angelich.models.post.SearchPostsResponse;
import ru.angelich.services.CommentService;
import ru.angelich.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(commentService.findAllByPostId(postId));
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("postId") Long postId,
                                                      @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(commentService.findById(postId, commentId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("postId") Long postId,
                                                         @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.createComment(postId, commentRequest));
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("postId") Long postId,
                                                         @PathVariable("commentId") Long commentId,
                                                         @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, commentRequest));
    }


    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.ok().build();
    }
}
