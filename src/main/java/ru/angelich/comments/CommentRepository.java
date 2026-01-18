package ru.angelich.comments;

import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.angelich.comments.models.Comment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @NonNull List<Comment> findAllByPostId(Long postId) {
        String sql = "select id, post_id, description from comment where post_id = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Comment.class), postId);
    }

    public Optional<Comment> findById(Long postId, Long commentId) {
        String sql = "select id, post_id, description from comment where post_id = ? and id = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Comment.class), postId, commentId)
                .stream()
                .findFirst();
    }

    public Comment saveComment(Long postId, Comment comment) {
        String sql = "insert into comments(post_id, description) values(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, postId);
            ps.setString(2, comment.getText());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            comment.setId(keyHolder.getKey().longValue());
        } else {
            throw new IllegalStateException("Не удалось получить ID нового поста");
        }
        return comment;
    }

    public Comment updateComment(Long postId, Long commentId, Comment comment) {
        jdbcTemplate.update("update comment set description = ? where id = ? and post_id = ?",
                comment.getText(), commentId, postId);
        return findById(postId, commentId).orElseThrow(
                () -> new IllegalStateException("Комментарий не найден"));
    }

    public void deleteComment(Long postId, Long commentId) {
        if (jdbcTemplate.update("delete from comment where id = ? and post_id = ?",
                commentId, postId) != 1) {
            throw new IllegalArgumentException("Комментарий не найден");
        }
    }
}
