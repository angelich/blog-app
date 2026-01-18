package ru.angelich.post;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(PostRequestDto postRequestDto) {
        return postRepository.save(PostMapper.INSTANCE.toPost(postRequestDto));
    }

    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));
    }

    public SearchPostsResponse searchPosts(String search, Integer pageNumber, Integer pageSize) {
        List<String> tags = new ArrayList<>();
        StringBuilder searchBuilder = new StringBuilder(search);
        Arrays.stream(search.split(" "))
                .map(String::trim)
                .forEach(word -> {
                            if (word.startsWith("#")) {
                                tags.add(word.substring(1));
                            } else {
                                searchBuilder.append(word).append(" "); //todo а точно так надо? мб теги тоже должны быть в поисковой строке?
                            }
                        }
                );
        String searchSubstring = searchBuilder.toString().trim();
        List<Post> posts = postRepository.searchPosts(searchSubstring, tags);

        return getSearchPostsResponse(pageNumber, pageSize, posts);
    }

    private static SearchPostsResponse getSearchPostsResponse(Integer pageNumber, Integer pageSize, List<Post> posts) {
        if (posts.isEmpty()) {
            return new SearchPostsResponse(emptyList(), false, false, 1);
        }

        int totalItems = posts.size();
        int lastPage = (int) Math.ceil((double) totalItems / pageSize);
        if (lastPage == 0) lastPage = 1;

        int skipCount = (pageNumber - 1) * pageSize;

        List<Post> pagedPosts = posts.stream()
                .skip(skipCount)
                .limit(pageSize)
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

    public @NonNull Post updatePost(Long id, PostRequestDto postRequestDto) {
        postRepository.update(id, PostMapper.INSTANCE.toPost(postRequestDto));
        return postRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Post not found"));
    }

    public void deletePost(Long id) {
        postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));

        postRepository.delete(id);
    }

    public @NonNull Post likePost(Long id) {
        var post = postRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Post not found"));
        postRepository.likePost(id);
        post.setLikesCount(post.getLikesCount() + 1);
        return post;
    }

    public void uploadImage(Long id, MultipartFile image) {
        postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));

        try (InputStream is = image.getInputStream()) {
            postRepository.uploadImage(id, is, image.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image file", e);
        }
    }

    @Transactional(readOnly = true)
    public void getImage(Long id, OutputStream outputStream) {
        postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Post not found"));

        postRepository.getImage(id, outputStream);
    }
}
