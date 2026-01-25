package ru.angelich.repositories;

import org.springframework.transaction.annotation.Transactional;
import ru.angelich.models.post.Post;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(Long id);

    Post save(Post post);

    List<Post> searchPosts(String searchSubstring, List<String> tags);

    @Transactional
    void update(Long id, Post post);

    void delete(Long id);

    void likePost(Long id);

    @Transactional
    void uploadImage(Long id, InputStream inputStream, long fileSize);

    void getImage(Long id, OutputStream outputStream);
}
