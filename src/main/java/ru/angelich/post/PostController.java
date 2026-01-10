package ru.angelich.post;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @GetMapping
    public ResponseEntity getAllPosts(@RequestParam String search,
                                      @RequestParam Integer pageNumber,
                                      @RequestParam Integer pageSize) {

        return null;
    }

    @PostMapping("/{id}")
    public ResponseEntity getPost(@PathVariable Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity createPost(@RequestBody Post post) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePost(@PathVariable Long id, @RequestBody Post post) {
        return null;
    }

    @DeleteMapping("/{id}") //удалить и комменты тоже
    public ResponseEntity deletePost(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity likePost(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/{id}/image")
    public ResponseEntity uploadImage(@PathVariable Long id, @RequestBody MultipartFile file) {
        return null;
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable Long id, @RequestBody MultipartFile file) {
/*        Resource file = filesService.download(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);*/
        return null;
    }
}
