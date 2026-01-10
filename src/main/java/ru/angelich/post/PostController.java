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
    public ResponseEntity getAllPosts(@RequestParam String search,
                                      @RequestParam Integer pageNumber,
                                      @RequestParam Integer pageSize) {

        return null;
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
