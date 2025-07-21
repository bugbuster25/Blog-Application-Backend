package com.example.blogapp.controller;

import com.example.blogapp.dto.UserDto;
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
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createUser_WithValidData_ReturnsCreatedUser() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("testuser")
                .fullName("Test User")
                .password("password123")
                .build();

        // Act
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", userDto, UserDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo(userDto.getUsername());
        assertThat(response.getBody().getFullName()).isEqualTo(userDto.getFullName());
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        // Act
        ResponseEntity<List> response = restTemplate.getForEntity("/api/users", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getUserById_WithExistingId_ReturnsUser() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("testuser2")
                .fullName("Test User 2")
                .password("password123")
                .build();
        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
        Long userId = createResponse.getBody().getId();

        // Act
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/api/users/" + userId, UserDto.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(userId);
        assertThat(response.getBody().getUsername()).isEqualTo(userDto.getUsername());
    }

    @Test
    void updateUser_WithValidData_ReturnsUpdatedUser() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("originaluser")
                .fullName("Original Name")
                .password("password123")
                .build();
        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
        Long userId = createResponse.getBody().getId();

        UserDto updateDto = UserDto.builder()
                .username("updateduser")
                .fullName("Updated Name")
                .password("newpassword123")
                .build();

        // Act
        ResponseEntity<UserDto> response = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                UserDto.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo(updateDto.getUsername());
        assertThat(response.getBody().getFullName()).isEqualTo(updateDto.getFullName());
    }

    @Test
    void deleteUser_WithExistingId_ReturnsNoContent() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("userToDelete")
                .fullName("Delete Me")
                .password("password123")
                .build();
        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
        Long userId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify the user is deleted
        ResponseEntity<UserDto> getResponse = restTemplate.getForEntity("/api/users/" + userId, UserDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}