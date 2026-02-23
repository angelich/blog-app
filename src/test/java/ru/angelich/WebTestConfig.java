package ru.angelich;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.angelich.controllers.CommentController;
import ru.angelich.controllers.PostController;
import ru.angelich.repositories.CommentRepository;
import ru.angelich.repositories.CommentRepositoryImpl;
import ru.angelich.repositories.PostRepository;
import ru.angelich.repositories.PostRepositoryImpl;
import ru.angelich.services.CommentService;
import ru.angelich.services.CommentServiceImpl;
import ru.angelich.services.PostService;
import ru.angelich.services.PostServiceImpl;

@Configuration
@EnableWebMvc
public class WebTestConfig {
    @Bean
    public PostController postController(PostService postService) {
        return new PostController(postService);
    }

    @Bean
    public CommentController commentController(CommentService commentService) {
        return new CommentController(commentService);
    }

    @Bean
    public PostService postService(PostRepository postRepository) {
        return new PostServiceImpl(postRepository);
    }

    @Bean
    public CommentService commentService(PostService postService, CommentRepository commentRepository) {
        return new CommentServiceImpl(postService, commentRepository);
    }

    @Bean
    public PostRepository postRepository(JdbcTemplate jdbcTemplate) {
        return new PostRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public CommentRepository commentRepository(JdbcTemplate jdbcTemplate) {
        return new CommentRepositoryImpl(jdbcTemplate);
    }
}
