package com.example.blogapp.controller;

import com.example.blogapp.dto.CommentDto;
import com.example.blogapp.dto.PostDto;
import com.example.blogapp.dto.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CommentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long userId;
    private Long postId;

    @BeforeEach
    void setUp() {
        // Create a test user
        UserDto userDto = UserDto.builder()
                .username("testuser")
                .fullName("Test User")
                .password("password123")
                .build();
        ResponseEntity<UserDto> userResponse = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
        userId = userResponse.getBody().getId();

        // Create a test post
        PostDto postDto = PostDto.builder()
                .title("Test Post")
                .content("Test Content")
                .userId(userId)
                .build();
        ResponseEntity<PostDto> postResponse = restTemplate.postForEntity("/api/posts", postDto, PostDto.class);
        postId = postResponse.getBody().getId();
    }

    @AfterEach
    void cleanUp() {
        // Delete all comments
        ResponseEntity<List> commentListResponse = restTemplate.getForEntity("/api/comments", List.class);
        if (commentListResponse.getBody() != null) {
            for (Object obj : commentListResponse.getBody()) {
                // Assuming response is a List of LinkedHashMaps representing JSON objects
                @SuppressWarnings("unchecked")
                var map = (java.util.LinkedHashMap<String, Object>) obj;
                Long commentId = ((Number) map.get("id")).longValue();
                restTemplate.delete("/api/comments/" + commentId);
            }
        }

        // Delete post
        if (postId != null) {
            restTemplate.delete("/api/posts/" + postId);
        }

        // Delete user
        if (userId != null) {
            restTemplate.delete("/api/users/" + userId);
        }
    }

    @Test
    void createComment_WithValidData_ReturnsCreatedComment() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test Comment")
                .postId(postId)
                .userId(userId)
                .build();

        // Act
        ResponseEntity<CommentDto> response = restTemplate.postForEntity("/api/comments", commentDto, CommentDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEqualTo(commentDto.getContent());
        assertThat(response.getBody().getPostId()).isEqualTo(postId);
        assertThat(response.getBody().getUserId()).isEqualTo(userId);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void getAllComments_ReturnsListOfComments() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test Comment")
                .postId(postId)
                .userId(userId)
                .build();
        restTemplate.postForEntity("/api/comments", commentDto, CommentDto.class);

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity("/api/comments", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThan(0);
    }

    @Test
    void getCommentById_WithExistingId_ReturnsComment() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Test Comment")
                .postId(postId)
                .userId(userId)
                .build();
        ResponseEntity<CommentDto> createResponse = restTemplate.postForEntity("/api/comments", commentDto, CommentDto.class);
        Long commentId = createResponse.getBody().getId();

        // Act
        ResponseEntity<CommentDto> response = restTemplate.getForEntity("/api/comments/" + commentId, CommentDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(commentId);
        assertThat(response.getBody().getContent()).isEqualTo(commentDto.getContent());
    }

    @Test
    void updateComment_WithValidData_ReturnsUpdatedComment() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Original Comment")
                .postId(postId)
                .userId(userId)
                .build();
        ResponseEntity<CommentDto> createResponse = restTemplate.postForEntity("/api/comments", commentDto, CommentDto.class);
        Long commentId = createResponse.getBody().getId();

        CommentDto updateDto = CommentDto.builder()
                .content("Updated Comment")
                .postId(postId)
                .userId(userId)
                .build();

        // Act
        ResponseEntity<CommentDto> response = restTemplate.exchange(
                "/api/comments/" + commentId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                CommentDto.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isEqualTo(updateDto.getContent());
    }

    @Test
    void deleteComment_WithExistingId_ReturnsNoContent() {
        // Arrange
        CommentDto commentDto = CommentDto.builder()
                .content("Comment to Delete")
                .postId(postId)
                .userId(userId)
                .build();
        ResponseEntity<CommentDto> createResponse = restTemplate.postForEntity("/api/comments", commentDto, CommentDto.class);
        Long commentId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/comments/" + commentId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the comment is deleted
        ResponseEntity<CommentDto> getResponse = restTemplate.getForEntity("/api/comments/" + commentId, CommentDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}