package ru.angelich.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.angelich.PostTestConfig;
import ru.angelich.models.post.Post;
import ru.angelich.models.post.PostRequest;
import ru.angelich.repositories.PostRepository;
import ru.angelich.services.PostService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = PostTestConfig.class)
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        reset(postRepository);
    }

    @Test
    void createPost_success() {
        var postRequest = new PostRequest("title", "text", List.of("tag1", "tag2"));
        var newPost = new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        when(postRepository.save(any())).thenReturn(newPost);

        var postResponse = postService.createPost(postRequest);

        assertEquals(1L, postResponse.id());
        assertEquals("title", postResponse.title());
        assertEquals("text", postResponse.text());
        assertEquals(List.of("tag1", "tag2"), postResponse.tags());
        assertEquals(0L, postResponse.likesCount());
        assertEquals(0L, postResponse.commentsCount());

        verify(postRepository, times(1)).save(any());
    }

    @Test
    void searchPosts_success() {
        var posts = List.of(new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L));

        when(postRepository.searchPosts(any(), any())).thenReturn(posts);

        var searchPostsResponse = postService.searchPosts("title #tag1", 1, 10);

        assertEquals(1, searchPostsResponse.posts().size());
        assertEquals(false, searchPostsResponse.hasNext());
        assertEquals(false, searchPostsResponse.hasPrev());
        assertEquals(1, searchPostsResponse.lastPage());
        assertEquals(1, searchPostsResponse.posts().getFirst().id());
        assertEquals("title", searchPostsResponse.posts().getFirst().title());
        assertEquals("text", searchPostsResponse.posts().getFirst().text());
        assertEquals(List.of("tag1", "tag2"), searchPostsResponse.posts().getFirst().tags());
        assertEquals(0L, searchPostsResponse.posts().getFirst().likesCount());
        assertEquals(0L, searchPostsResponse.posts().getFirst().commentsCount());

        verify(postRepository, times(1)).searchPosts(any(), any());
    }

    @Test
    void updatePost_success() {
        var postRequest = new PostRequest("newTitle", "newText", List.of("newTag1", "newTag2"));
        var post = new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        var updatedPost = postService.updatePost(1L, postRequest);

        assertEquals(post.getId(), updatedPost.id());
        assertEquals(post.getTitle(), updatedPost.title());
        assertEquals(post.getDescription(), updatedPost.text());
        assertEquals(post.getTags(), updatedPost.tags());
        assertEquals(post.getLikesCount(), updatedPost.likesCount());
        assertEquals(post.getCommentsCount(), updatedPost.commentsCount());

        verify(postRepository, times(1)).update(any(), any());
    }

    @Test
    void updatePost_postNotFound() {
        var postRequest = new PostRequest("newTitle", "newText", List.of("newTag1", "newTag2"));

        when(postRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(1L, postRequest));
        verify(postRepository, never()).update(any(), any());
    }


    @Test
    void deletePost_success() {
        var post = new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.deletePost(1L));
    }

    @Test
    void deletePost_postNotFound() {
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.deletePost(1L));
        verify(postRepository, never()).delete(any());
    }

    @Test
    void likePost() {
        var post = new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        var postResponse = postService.likePost(1L);

        assertEquals(1L, postResponse.likesCount());
    }

    @Test
    void uploadImage_success() {
        var post = new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L);

        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        doNothing().when(postRepository).uploadImage(any(), any(InputStream.class), eq(image.getSize()));

        postService.uploadImage(post.getId(), image);

        verify(postRepository, times(1)).uploadImage(eq(post.getId()), any(InputStream.class), eq(image.getSize()));
    }

    @Test
    void getImage_success() {
        var post = new Post(1L, "title", "text", List.of("tag1", "tag2"), 0L, 0L);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        byte[] imageData = "fake-image-data".getBytes();

        doAnswer(invocation -> {
            OutputStream os = invocation.getArgument(1);
            os.write(imageData);
            return null;
        }).when(postRepository).getImage(any(), any(OutputStream.class));

        assertDoesNotThrow(() -> postService.getImage(1L, mock(OutputStream.class)));
    }
}