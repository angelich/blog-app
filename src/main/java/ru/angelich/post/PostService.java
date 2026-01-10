package ru.angelich.post;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(PostRequestDto postRequestDto) {
        return postRepository.save(PostMapper.INSTANCE.toPost(postRequestDto));
    }

    @NonNull
    public Post getPost(Long id) {
        return postRepository.findById(id);
    }
}
