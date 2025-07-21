package com.example.blogapp.service.impl;

import com.example.blogapp.dto.CommentDto;
import com.example.blogapp.dto.PostDto;
import com.example.blogapp.entity.Post;
import com.example.blogapp.entity.User;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.repository.PostRepository;
import com.example.blogapp.repository.UserRepository;
import com.example.blogapp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDto(post);
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", postDto.getUserId()));

        Post post = mapToEntity(postDto);
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        return mapToDto(savedPost);
    }

    @Override
    public PostDto updatePost(Long id, PostDto postDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", postDto.getUserId()));

        existingPost.setTitle(postDto.getTitle());
        existingPost.setContent(postDto.getContent());
        existingPost.setUser(user);

        Post updatedPost = postRepository.save(existingPost);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        postRepository.deleteById(id);
    }

    // Mapping helpers
    private PostDto mapToDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                // To show comments in the post:
                .comments(
                        post.getComments() != null ? post.getComments().stream().map(comment -> CommentDto.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .postId(comment.getPost().getId())
                                .userId(comment.getUser().getId())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()) : null
                )
                .build();
    }

    private Post mapToEntity(PostDto postDto) {
        return Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .build();
    }
}
