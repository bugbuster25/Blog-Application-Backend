package com.example.blogapp.service.impl;

import com.example.blogapp.dto.CommentDto;
import com.example.blogapp.entity.Comment;
import com.example.blogapp.entity.Post;
import com.example.blogapp.entity.User;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.repository.CommentRepository;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Post post;
    private Comment comment;
    private CommentDto commentDto;

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
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("Test Comment")
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Test Comment")
                .postId(1L)
                .userId(1L)
                .build();
    }

    @Test
    void getAllComments_ShouldReturnListOfCommentDtos() {
        // Given
        List<Comment> comments = Arrays.asList(comment);
        given(commentRepository.findAll()).willReturn(comments);

        // When
        List<CommentDto> commentDtos = commentService.getAllComments();

        // Then
        assertThat(commentDtos).isNotEmpty();
        assertThat(commentDtos.size()).isEqualTo(1);
        verify(commentRepository).findAll();
    }

    @Test
    void getAllComments_WhenNoCommentsExist_ShouldReturnEmptyList() {
        given(commentRepository.findAll()).willReturn(Collections.emptyList());

        List<CommentDto> commentDtos = commentService.getAllComments();

        assertThat(commentDtos).isEmpty();
        verify(commentRepository).findAll();
    }

    @Test
    void getCommentById_WithValidId_ShouldReturnCommentDto() {
        // Given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // When
        CommentDto foundComment = commentService.getCommentById(1L);

        // Then
        assertThat(foundComment).isNotNull();
        assertThat(foundComment.getId()).isEqualTo(comment.getId());
        verify(commentRepository).findById(1L);
    }

    @Test
    void getCommentById_WithInvalidId_ShouldThrowException() {
        // Given
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(1L));
        verify(commentRepository).findById(1L);
    }

    @Test
    void createComment_WithValidData_ShouldReturnCreatedCommentDto() {
        // Given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        // When
        CommentDto createdComment = commentService.createComment(commentDto);

        // Then
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getContent()).isEqualTo(commentDto.getContent());
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_WithInvalidPostId_ShouldThrowException() {
        // Given
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(commentDto));
        verify(postRepository).findById(1L);
        verify(userRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WithInvalidUserId_ShouldThrowException() {
        // Given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(commentDto));
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WithValidData_ShouldReturnUpdatedCommentDto() {
        // Given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        CommentDto updateDto = CommentDto.builder()
                .content("Updated Comment")
                .postId(1L)
                .userId(1L)
                .build();

        // When
        CommentDto updatedComment = commentService.updateComment(1L, updateDto);

        // Then
        assertThat(updatedComment).isNotNull();
        verify(commentRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void updateComment_WithInvalidCommentId_ShouldThrowException() {
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        CommentDto updateDto = CommentDto.builder()
                .content("Updated Comment")
                .postId(1L)
                .userId(1L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, updateDto));
        verify(commentRepository).findById(1L);
        verify(postRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WithInvalidPostId_ShouldThrowException() {
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(postRepository.findById(1L)).willReturn(Optional.empty());

        CommentDto updateDto = CommentDto.builder()
                .content("Updated Comment")
                .postId(1L)
                .userId(1L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, updateDto));
        verify(commentRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(userRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_WithInvalidUserId_ShouldThrowException() {
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        CommentDto updateDto = CommentDto.builder()
                .content("Updated Comment")
                .postId(1L)
                .userId(1L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, updateDto));
        verify(commentRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        given(commentRepository.existsById(1L)).willReturn(true);
        doNothing().when(commentRepository).deleteById(1L);

        // When
        commentService.deleteComment(1L);

        // Then
        verify(commentRepository).existsById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteComment_WithInvalidId_ShouldThrowException() {
        // Given
        given(commentRepository.existsById(1L)).willReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L));
        verify(commentRepository).existsById(1L);
        verify(commentRepository, never()).deleteById(any());
    }
}