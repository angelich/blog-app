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
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<SearchPostsResponse> searchPosts(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize) {

        return ResponseEntity.ok(postService.searchPosts(search, pageNumber, pageSize));
    }

    @GetMapping("/{postId}")
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

    @PutMapping("/{postId}/image")
    public ResponseEntity<Void> uploadImage(@PathVariable("postId") Long postId,
                                            @RequestParam("image") MultipartFile image) {
        postService.uploadImage(postId, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<StreamingResponseBody> getImage(@PathVariable("postId") Long postId) {
        StreamingResponseBody responseBody = outputStream -> postService.getImage(postId, outputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(responseBody);
    }
}
