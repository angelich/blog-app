package ru.angelich.posts;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.angelich.posts.models.Post;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public PostRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "select id, title, description, likes_count, " +
                "(select count(*) from comments where post_id = ?) as comments_count " +
                "from posts where id = ?";
        Optional<Post> post = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Post.class), id, id)
                .stream()
                .findFirst();

        post.ifPresent(p ->
                p.setTags(jdbcTemplate.queryForList("select tag from tags where post_id = ?", String.class, id)));

        return post;
    }

    @Override
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

    @Override
    public List<Post> searchPosts(String searchSubstring, List<String> tags) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, title, description, likes_count, " +
                        "(SELECT COUNT(*) FROM comments WHERE post_id = posts.id) AS comments_count " +
                        "FROM posts WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        if (searchSubstring != null && !searchSubstring.isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + searchSubstring + "%");
        }

        if (tags != null && !tags.isEmpty()) {
            sql.append(" AND posts.id IN (SELECT post_id FROM tags WHERE tag = ANY(?))");
            params.add(tags.toArray());
        }

        List<Post> posts = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Post.class), params.toArray());

        posts.forEach(post -> post.setTags(
                jdbcTemplate.queryForList("SELECT tag FROM tags WHERE post_id = ?", String.class, post.getId())
        ));

        return posts;
    }

    @Transactional
    @Override
    public void update(Long id, Post post) {
        jdbcTemplate.update("delete from tags where post_id=?", id);
        jdbcTemplate.update("update posts set title=?, description=? where id=?", post.getTitle(), post.getDescription(), id);
        for (String tag : post.getTags()) {
            jdbcTemplate.update("insert into tags(post_id, tag) values(?, ?)", post.getId(), tag);
        }
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("delete from posts where post_id = ?", id);
    }

    @Override
    public void likePost(Long id) {
        jdbcTemplate.update("update posts set likes_count = likes_count + 1 where id = ?", id);
    }

    @Transactional
    @Override
    public void uploadImage(Long id, InputStream inputStream, long fileSize) {
        String sql = "update posts set image = ? WHERE id = ?";

        jdbcTemplate.update(sql, ps -> {
            ps.setBinaryStream(1, inputStream, fileSize);
            ps.setLong(2, id);
        });
    }

    @Override
    public void getImage(Long id, OutputStream outputStream) {
        String sql = "select image from posts WHERE id = ?";

        jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                try (InputStream is = rs.getBinaryStream("image")) {
                    if (is != null) {
                        is.transferTo(outputStream);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при чтении потока из БД", e);
                }
            }
            return null;
        }, id);
    }
}