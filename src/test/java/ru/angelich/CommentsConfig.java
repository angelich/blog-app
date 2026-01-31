package ru.angelich;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.angelich.repositories.CommentRepository;
import ru.angelich.services.CommentService;
import ru.angelich.services.CommentServiceImpl;
import ru.angelich.services.PostService;

@Configuration
public class CommentsConfig {

    @Bean
    public CommentService commentService(PostService postService, CommentRepository commentRepository) {
        return new CommentServiceImpl(postService, commentRepository);
    }

    @Bean
    public CommentRepository commentRepository() {
        return Mockito.mock(CommentRepository.class);
    }

    @Bean
    public PostService postService() {
        return Mockito.mock(PostService.class);
    }
}
