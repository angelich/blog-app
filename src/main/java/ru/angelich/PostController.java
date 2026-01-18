package ru.angelich;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.angelich.comments.CommentService;
import ru.angelich.comments.models.CommentRequest;
import ru.angelich.comments.models.CommentResponse;
import ru.angelich.posts.PostService;
import ru.angelich.posts.models.PostRequest;
import ru.angelich.posts.models.PostResponse;
import ru.angelich.posts.models.SearchPostsResponse;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<SearchPostsResponse> searchPosts(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize) {

        return ResponseEntity.ok(postService.searchPosts(search, pageNumber, pageSize));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest) {
        return ResponseEntity.ok(postService.createPost(postRequest));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable("postId") Long postId,
                                                   @RequestBody PostRequest postRequest) {
        return ResponseEntity.ok(postService.updatePost(postId, postRequest));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostResponse> likePost(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.likePost(postId));
    }

    @PostMapping("/{postId}/image")
    public ResponseEntity<Void> uploadImage(@PathVariable("postId") Long postId,
                                            @RequestParam("image") MultipartFile image) {
        postService.uploadImage(postId, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<StreamingResponseBody> getImage(@PathVariable("postId") Long postId) {
        StreamingResponseBody responseBody = outputStream -> postService.getImage(postId, outputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
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
