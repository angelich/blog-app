package ru.angelich.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.angelich.models.post.*;
import ru.angelich.repositories.PostRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class PostServiceImpl implements PostService {
    public static final int MAX_TEXT_LENGTH = 128;
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    static SearchPostsResponse getSearchPostsResponse(Integer pageNumber, Integer pageSize, List<Post> posts) {
        if (posts.isEmpty()) {
            return new SearchPostsResponse(emptyList(), false, false, 1);
        }

        int totalItems = posts.size();
        int lastPage = (int) Math.ceil((double) totalItems / pageSize);
        if (lastPage == 0) lastPage = 1;

        int skipCount = (pageNumber - 1) * pageSize;

        List<PostResponse> pagedPosts = posts.stream()
                .skip(skipCount)
                .limit(pageSize)
                .peek(post -> {
                    if (post.getDescription().length() > PostServiceImpl.MAX_TEXT_LENGTH) {
                        post.setDescription(post.getDescription().substring(0, PostServiceImpl.MAX_TEXT_LENGTH) + "...");
                    }
                })
                .map(PostMapper.INSTANCE::toPostResponse)
                .toList();

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        return new SearchPostsResponse(
                pagedPosts,
                hasPrev,
                hasNext,
                lastPage
        );
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        var post = postRepository.save(PostMapper.INSTANCE.toPost(postRequest));
        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @Override
    public PostResponse getPost(Long id) {
        return PostMapper.INSTANCE.toPostResponse(getPostByIdOrThrow(id));
    }

    @Override
    public SearchPostsResponse searchPosts(String search, Integer pageNumber, Integer pageSize) {
        List<String> tags = new ArrayList<>();
        StringBuilder searchBuilder = new StringBuilder();
        Arrays.stream(search.split(" "))
                .map(String::trim)
                .forEach(word -> {
                            if (word.startsWith("#")) {
                                tags.add(word.substring(1));
                            } else {
                                searchBuilder.append(word).append(" ");
                            }
                        }
                );
        String searchSubstring = searchBuilder.toString().trim();
        List<Post> posts = postRepository.searchPosts(searchSubstring, tags);

        return getSearchPostsResponse(pageNumber, pageSize, posts);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        postRepository.update(id, PostMapper.INSTANCE.toPost(postRequest));
        return PostMapper.INSTANCE.toPostResponse(getPostByIdOrThrow(id));
    }

    @Override
    public void deletePost(Long id) {
        postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));

        postRepository.delete(id);
    }

    @Override
    public PostResponse likePost(Long id) {
        var post = getPostByIdOrThrow(id);
        postRepository.likePost(id);
        post.setLikesCount(post.getLikesCount() + 1);
        return PostMapper.INSTANCE.toPostResponse(post);
    }

    @Override
    public void uploadImage(Long id, MultipartFile image) {
        getPostByIdOrThrow(id);

        try (InputStream is = image.getInputStream()) {
            postRepository.uploadImage(id, is, image.getSize());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to process image file", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public void getImage(Long id, OutputStream outputStream) {
        getPostByIdOrThrow(id);
        postRepository.getImage(id, outputStream);
    }

    @Override
    public Post getPostByIdOrThrow(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));
    }
}
