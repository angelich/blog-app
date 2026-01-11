package ru.angelich.post;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity updatePost(@PathVariable("id") Long id, @RequestBody Post post) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePost(@PathVariable("id") Long id) {
        return null;
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity likePost(@PathVariable("id") Long id) {
        return null;
    }

    @PostMapping("/{id}/image")
    public ResponseEntity uploadImage(@PathVariable("id") Long id, @RequestBody MultipartFile file) {
        return null;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable("id") Long id) {
/*        Resource file = filesService.download(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);*/
        return null;
    }
}
