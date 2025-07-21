package com.example.blogapp.controller;

import com.example.blogapp.dto.PostDto;
import com.example.blogapp.dto.UserDto;
import com.example.blogapp.repository.PostRepository;
import com.example.blogapp.repository.UserRepository;
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
class PostControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long userId;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userId = createTestUser();
    }

    @AfterEach
    void tearDown() {
        // Clean up posts and users after each test
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Long createTestUser() {
        UserDto userDto = UserDto.builder()
                .username("testuser")
                .fullName("Test User")
                .password("password")
                .build();

        ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().getId();
    }

    @Test
    void createPost_WithValidData_ReturnsCreatedPost() {
        PostDto postDto = PostDto.builder()
                .title("Test Post")
                .content("Test Content")
                .userId(userId)
                .build();

        ResponseEntity<PostDto> response = restTemplate.postForEntity("/api/posts", postDto, PostDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(postDto.getTitle());
        assertThat(response.getBody().getContent()).isEqualTo(postDto.getContent());
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void getAllPosts_ReturnsListOfPosts() {
        // Act
        ResponseEntity<List> response = restTemplate.getForEntity("/api/posts", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getPostById_WithExistingId_ReturnsPost() {
        PostDto postDto = PostDto.builder()
                .title("Test Post")
                .content("Test Content")
                .userId(userId)
                .build();
        ResponseEntity<PostDto> createResponse = restTemplate.postForEntity("/api/posts", postDto, PostDto.class);
        Long postId = createResponse.getBody().getId();

        ResponseEntity<PostDto> response = restTemplate.getForEntity("/api/posts/" + postId, PostDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(postId);
    }

    @Test
    void updatePost_WithValidData_ReturnsUpdatedPost() {
        PostDto postDto = PostDto.builder()
                .title("Original Title")
                .content("Original Content")
                .userId(userId)
                .build();
        ResponseEntity<PostDto> createResponse = restTemplate.postForEntity("/api/posts", postDto, PostDto.class);
        Long postId = createResponse.getBody().getId();

        PostDto updateDto = PostDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .userId(userId)
                .build();

        ResponseEntity<PostDto> response = restTemplate.exchange(
                "/api/posts/" + postId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                PostDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(response.getBody().getContent()).isEqualTo(updateDto.getContent());
    }

    @Test
    void deletePost_WithExistingId_ReturnsNoContent() {
        PostDto postDto = PostDto.builder()
                .title("Test Post")
                .content("Test Content")
                .userId(userId)
                .build();
        ResponseEntity<PostDto> createResponse = restTemplate.postForEntity("/api/posts", postDto, PostDto.class);
        Long postId = createResponse.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/posts/" + postId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<PostDto> getResponse = restTemplate.getForEntity("/api/posts/" + postId, PostDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
