package com.example.blogapp.service.impl;

import com.example.blogapp.dto.PostDto;
import com.example.blogapp.entity.Post;
import com.example.blogapp.entity.User;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.repository.PostRepository;
import com.example.blogapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("Test Content")
                .user(user)
                .comments(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postDto = PostDto.builder()
                .id(1L)
                .title("Test Post")
                .content("Test Content")
                .userId(1L)
                .comments(Collections.emptyList())
                .build();
    }

    @Test
    void getAllPosts_ShouldReturnListOfPostDtos() {
        // Given
        List<Post> posts = Arrays.asList(post);
        given(postRepository.findAll()).willReturn(posts);

        // When
        List<PostDto> postDtos = postService.getAllPosts();

        // Then
        assertThat(postDtos).isNotEmpty();
        assertThat(postDtos.size()).isEqualTo(1);
        verify(postRepository).findAll();
    }

    @Test
    void getAllPosts_WhenNoPostsExist_ShouldReturnEmptyList() {
        given(postRepository.findAll()).willReturn(Collections.emptyList());

        List<PostDto> postDtos = postService.getAllPosts();

        assertThat(postDtos).isEmpty();
        verify(postRepository).findAll();
    }

    @Test
    void getPostById_WithValidId_ShouldReturnPostDto() {
        // Given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // When
        PostDto foundPost = postService.getPostById(1L);

        // Then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getId()).isEqualTo(post.getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void getPostById_WithInvalidId_ShouldThrowException() {
        // Given
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository).findById(1L);
    }



    @Test
    void createPost_WithValidData_ShouldReturnCreatedPostDto() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willReturn(post);

        // When
        PostDto createdPost = postService.createPost(postDto);

        // Then
        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo(postDto.getTitle());
        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_WithInvalidUserId_ShouldThrowException() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(postDto));
        verify(userRepository).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_WithValidData_ShouldReturnUpdatedPostDto() {
        // Given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willReturn(post);

        PostDto updateDto = PostDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .userId(1L)
                .build();

        // When
        PostDto updatedPost = postService.updatePost(1L, updateDto);

        // Then
        assertThat(updatedPost).isNotNull();
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePost_WithInvalidPostId_ShouldThrowException() {
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        PostDto updateDto = PostDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .userId(1L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(1L, updateDto));
        verify(postRepository).findById(1L);
        verify(userRepository, never()).findById(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePost_WithInvalidUserId_ShouldThrowException() {
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        PostDto updateDto = PostDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .userId(1L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(1L, updateDto));
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(postRepository, never()).save(any());
    }

    @Test
    void deletePost_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        given(postRepository.existsById(1L)).willReturn(true);
        doNothing().when(postRepository).deleteById(1L);

        // When
        postService.deletePost(1L);

        // Then
        verify(postRepository).existsById(1L);
        verify(postRepository).deleteById(1L);
    }

    @Test
    void deletePost_WithInvalidId_ShouldThrowException() {
        // Given
        given(postRepository.existsById(1L)).willReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L));
        verify(postRepository).existsById(1L);
        verify(postRepository, never()).deleteById(any());
    }
}