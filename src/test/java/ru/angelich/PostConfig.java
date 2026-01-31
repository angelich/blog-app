package ru.angelich;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.angelich.repositories.PostRepository;
import ru.angelich.services.PostService;
import ru.angelich.services.PostServiceImpl;

@Configuration
public class PostConfig {

    @Bean
    public PostService postService(PostRepository postRepository) {
        return new PostServiceImpl(postRepository);
    }

    @Bean
    public PostRepository postRepository() {
        return Mockito.mock(PostRepository.class);
    }
}
