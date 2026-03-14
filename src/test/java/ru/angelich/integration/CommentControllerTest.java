package ru.angelich.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CommentControllerTest {

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
