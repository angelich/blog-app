package ru.angelich.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.angelich.DataSourceTestConfig;
import ru.angelich.WebTestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringJUnitConfig(classes = {DataSourceTestConfig.class, WebTestConfig.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class PostControllerTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("""
                INSERT INTO posts (id, title, description)
                VALUES (100,'Заголовок поста','Текст поста в формате Markdown...'),
                (101,'Заголовок поста','Текст поста в формате Markdown...')
                """);
    }

    private void setUpSearchTestData() {
        jdbcTemplate.execute("DELETE FROM tags");
        jdbcTemplate.execute("""
                    INSERT INTO posts (id, title, description, likes_count) VALUES
                    (200, 'Java Guide', 'Learn Java programming', 0),
                    (201, 'Spring Boot Tips', 'Tips for Spring Boot development', 1),
                    (202, 'Advanced Java', 'Deep dive into Java concurrency', 2),
                    (203, 'Python for Beginners', 'Intro to Python', 3)
                """);

        jdbcTemplate.execute("""
                    INSERT INTO tags (post_id, tag) VALUES
                    (200, 'java'), (200, 'programming'),
                    (201, 'spring'), (201, 'java'), (201, 'tips'),
                    (202, 'java'), (202, 'concurrency'),
                    (203, 'python'), (203, 'beginners')
                """);
    }

    @Test
    void searchPosts_byTitle() throws Exception {
        setUpSearchTestData();

        mockMvc.perform(get("/api/posts")
                        .param("search", "Java")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[0].title").value("Java Guide"))
                .andExpect(jsonPath("$.posts[0].likesCount").value(0))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(0))
                .andExpect(jsonPath("$.posts[1].title").value("Advanced Java"))
                .andExpect(jsonPath("$.posts[1].likesCount").value(2))
                .andExpect(jsonPath("$.posts[1].commentsCount").value(0))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void searchPosts_pagination() throws Exception {
        setUpSearchTestData();

        mockMvc.perform(get("/api/posts")
                        .param("search", "Java")
                        .param("pageNumber", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("Java Guide"))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.lastPage").value(2));

        mockMvc.perform(get("/api/posts")
                        .param("search", "Java")
                        .param("pageNumber", "2")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("Advanced Java"))
                .andExpect(jsonPath("$.hasPrev").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(2));
    }

    @Test
    void searchPosts_byTag() throws Exception {
        setUpSearchTestData();

        mockMvc.perform(get("/api/posts")
                        .param("search", "#java")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(3))
                .andExpect(jsonPath("$.posts[0].title").value("Java Guide"))
                .andExpect(jsonPath("$.posts[1].title").value("Spring Boot Tips"))
                .andExpect(jsonPath("$.posts[2].title").value("Advanced Java"));
    }

    @Test
    void searchPosts_noResults() throws Exception {
        setUpSearchTestData();

        mockMvc.perform(get("/api/posts")
                        .param("search", "Kotlin")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(0))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void createPost_success() throws Exception {
        String json = """
                  {
                    "title": "Название поста",
                    "text": "Текст поста в формате Markdown...",
                    "tags": ["tag_1", "tag_2"]
                  }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Название поста"))
                .andExpect(jsonPath("$.text").value("Текст поста в формате Markdown..."))
                .andExpect(jsonPath("$.tags").isArray());
    }

    @Test
    void updatePost_success() throws Exception {
        String json = """
                  {
                    "title": "Измененное название поста",
                    "text": "Измененный текст поста в формате Markdown...",
                    "tags": ["tag_3", "tag_4"]
                  }
                """;

        mockMvc.perform(put("/api/posts/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("Измененное название поста"))
                .andExpect(jsonPath("$.text").value("Измененный текст поста в формате Markdown..."))
                .andExpect(jsonPath("$.tags").isArray());
    }

    @Test
    void deletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/100"))
                .andExpect(status().isOk());
    }

    @Test
    void likePost() throws Exception {
        mockMvc.perform(post("/api/posts/100/likes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.likesCount").value(1));
    }

    @Test
    void uploadImage_success() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", pngStub);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/posts/{postId}/image", 101L)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{postId}/image", 101L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void createComment_success() throws Exception {
        String newCommentJson = """
                {
                    "text": "Новый комментарий",
                    "postId": 100
                }
                """;

        mockMvc.perform(post("/api/posts/{postId}/comments", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCommentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Новый комментарий"))
                .andExpect(jsonPath("$.postId").value(100));


        mockMvc.perform(get("/api/posts/{postId}/comments", 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].text").value("Новый комментарий"))
                .andExpect(jsonPath("$[0].postId").value(100));
    }

    @Test
    void updateComment_success() throws Exception {
        jdbcTemplate.update("INSERT INTO comments (id, description, post_id) VALUES " +
                "(?, ?, ?)", 100, "Текст комментария", "100");


        String updatedCommentJson = """
                {
                    "text": "Обновлённый текст комментария",
                    "postId": 100
                }
                """;

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", 100L, 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCommentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.text").value("Обновлённый текст комментария"))
                .andExpect(jsonPath("$.postId").value(100));


        mockMvc.perform(get("/api/posts/{postId}/comments", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].text").value("Обновлённый текст комментария"));
    }

    @Test
    void deleteComment_success() throws Exception {
        jdbcTemplate.update("INSERT INTO comments (id, description, post_id) VALUES " +
                "(?, ?, ?)", 100, "Текст комментария", "100");

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", 100L, 100L));
    }
}
