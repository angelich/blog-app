package ru.angelich.post;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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

    @PostMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequestDto postRequestDto) {
        return ResponseEntity.ok(postService.createPost(postRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @RequestBody PostRequestDto postRequestDto) {
        return ResponseEntity.ok(postService.updatePost(id, postRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Post> likePost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.likePost(id));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Void> uploadImage(@PathVariable("id") Long id, @RequestParam("image") MultipartFile image) {
        postService.uploadImage(id, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<StreamingResponseBody> getImage(@PathVariable("id") Long id) {
        StreamingResponseBody responseBody = outputStream -> postService.getImage(id, outputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}
