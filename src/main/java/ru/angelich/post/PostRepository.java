package ru.angelich.post;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository { // TODO: Вынести в интерфейс
    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Post> findById(Long id) {
        String sql = "select id, title, description, likes_count, " +
                "(select count(*) from comments where post_id = ?) as comments_count " +
                "from posts where id = ?";
        Optional<Post> post = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), id, id)
                .stream()
                .findFirst();

        post.ifPresent(p -> {
            p.setTags(jdbcTemplate.queryForList("select tag from tags where post_id = ?", String.class, id));
        });

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

    /// TODO описание если больше 128 символов, то обрезается до 128 символов и добавляется «…»
    public List<Post> searchPosts(String searchSubstring, List<String> tags) {
        String sql = "select id, title, description, likes_count, " +
                "(select count(*) from comments where post_id = posts.id ) as comments_count " +
                "from posts " +
                "where title like ? and posts.id in (select post_id from tags where tag in (?))";

        List<Post> posts = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), searchSubstring, tags);

        posts.forEach(post -> post.setTags(
                jdbcTemplate.queryForList("select tag from tags where post_id = ?", String.class, post.getId())));

        return posts;
    }

    @Transactional
    public void update(Long id, Post post) {
        jdbcTemplate.update("delete from tags where post_id=?", id);
        jdbcTemplate.update("update post set title=?, description=? where id=?", post.getTitle(), post.getDescription(), id);
        for (String tag : post.getTags()) {
            jdbcTemplate.update("insert into tags(post_id, tag) values(?, ?)", post.getId(), tag);
        }
    }

    public void delete(Long id) {
        jdbcTemplate.update("delete from post where post_id=?", id);
    }
}
