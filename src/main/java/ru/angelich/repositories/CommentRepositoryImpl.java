package ru.angelich.repositories;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.angelich.models.comment.Comment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> findAllByPostId(Long postId) {
        String sql = "select id, post_id, description from comments where post_id = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Comment.class), postId);
    }

    @Override
    public Optional<Comment> findById(Long postId, Long commentId) {
        String sql = "select id, post_id, description from comments where post_id = ? and id = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Comment.class), postId, commentId)
                .stream()
                .findFirst();
    }

    @Override
    public Comment saveComment(Long postId, Comment comment) {
        String sql = "insert into comments(post_id, description) values(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, postId);
            ps.setString(2, comment.getDescription());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            comment.setId(keyHolder.getKey().longValue());
        } else {
            throw new IllegalStateException("Не удалось получить ID нового поста");
        }
        return comment;
    }

    @Override
    public Comment updateComment(Long postId, Long commentId, Comment comment) {
        jdbcTemplate.update("update comments set description = ? where id = ? and post_id = ?",
                comment.getDescription(), commentId, postId);
        return findById(postId, commentId).orElseThrow(
                () -> new IllegalStateException("Комментарий не найден"));
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        if (jdbcTemplate.update("delete from comments where id = ? and post_id = ?",
                commentId, postId) != 1) {
            throw new IllegalArgumentException("Комментарий не найден");
        }
    }
}
