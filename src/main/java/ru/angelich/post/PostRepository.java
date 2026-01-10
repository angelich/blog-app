package ru.angelich.post;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class PostRepository { // TODO: Вынести в интерфейс
    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Post findById(Long id) {
        String sql = "select id, title, description, likes_count, " +
                "(select count(*) from comments where post_id = ?) as comments_count " +
                "from posts where id = ?";
        Post post = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), id, id)
                .stream()
                .findFirst()
                .orElse(null);

        if (post != null) {
            post.setTags(jdbcTemplate.queryForList("select tag from tags where post_id = ?", String.class, id));
        }

        return post;
    }

    public Post save(Post post) {
        String sql = "insert into posts(title, description) values(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            post.setId(keyHolder.getKey().longValue());
        } else {
            throw new IllegalStateException("Не удалось получить ID нового поста");
        }

        if (post.getTags() != null) {
            for (String tag : post.getTags()) {
                jdbcTemplate.update("insert into tags(post_id, tag) values(?, ?)", post.getId(), tag);
            }
        }

        return post;
    }
}
