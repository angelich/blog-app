package ru.angelich.post;

import org.springframework.stereotype.Service;

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
        return postRepository.findById(id);
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
}
