package com.example.blogapp.service.impl;

import com.example.blogapp.dto.UserDto;
import com.example.blogapp.entity.User;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        given(userRepository.findAll()).willReturn(Arrays.asList(user));

        List<UserDto> userDtos = userService.getAllUsers();

        assertThat(userDtos).isNotEmpty();
        assertThat(userDtos.size()).isEqualTo(1);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        given(userRepository.findAll()).willReturn(Collections.emptyList());

        List<UserDto> userDtos = userService.getAllUsers();

        assertThat(userDtos).isEmpty();
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUserDto() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        UserDto foundUser = userService.getUserById(1L);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUserDto() {
        given(userRepository.save(any(User.class))).willReturn(user);

        UserDto createdUser = userService.createUser(userDto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(userDto.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithValidId_ShouldReturnUpdatedUserDto() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(user);

        UserDto updateDto = UserDto.builder()
                .username("updatedUser")
                .build();

        UserDto updatedUser = userService.updateUser(1L, updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getUsername()).isEqualTo("updatedUser");
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowException() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        UserDto updateDto = UserDto.builder()
                .username("updatedUser")
                .build();

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateDto));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteSuccessfully() {
        given(userRepository.existsById(1L)).willReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        given(userRepository.existsById(1L)).willReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }
}
