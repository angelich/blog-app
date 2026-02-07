package ru.angelich;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.angelich.repositories.CommentRepository;
import ru.angelich.repositories.PostRepository;
import ru.angelich.services.CommentService;
import ru.angelich.services.CommentServiceImpl;
import ru.angelich.services.PostService;
import ru.angelich.services.PostServiceImpl;

@Configuration
public class UnitTestConfig {

    @Bean
    public PostRepository postRepository() {
        return Mockito.mock(PostRepository.class);
    }

    @Bean
    public CommentRepository commentRepository() {
        return Mockito.mock(CommentRepository.class);
    }

    @Bean
    public PostService postService(PostRepository postRepository) {
        return new PostServiceImpl(postRepository);
    }

    @Bean
    public CommentService commentService(PostService postService, CommentRepository commentRepository) {
        return new CommentServiceImpl(postService, commentRepository);
    }
}