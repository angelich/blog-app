package ru.angelich.posts;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.angelich.posts.models.Post;
import ru.angelich.posts.models.PostRequest;
import ru.angelich.posts.models.PostResponse;
import ru.angelich.posts.models.SearchPostsResponse;

import java.io.OutputStream;

public interface PostService {

    PostResponse createPost(PostRequest postRequest);

    PostResponse getPost(Long id);

    SearchPostsResponse searchPosts(String search, Integer pageNumber, Integer pageSize);

    PostResponse updatePost(Long id, PostRequest postRequest);

    void deletePost(Long id);

    PostResponse likePost(Long id);

    void uploadImage(Long id, MultipartFile image);

    @Transactional(readOnly = true)
    void getImage(Long id, OutputStream outputStream);

    Post getPostByIdOrThrow(Long id);
}
