package com.example.blogapp.service.impl;

import com.example.blogapp.dto.CommentDto;
import com.example.blogapp.entity.Comment;
import com.example.blogapp.entity.Post;
import com.example.blogapp.entity.User;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.repository.CommentRepository;
import com.example.blogapp.repository.PostRepository;
import com.example.blogapp.repository.UserRepository;
import com.example.blogapp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        return mapToDto(comment);
    }

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", commentDto.getPostId()));

        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", commentDto.getUserId()));

        Comment comment = mapToEntity(commentDto);
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    @Override
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", commentDto.getPostId()));

        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", commentDto.getUserId()));

        existingComment.setContent(commentDto.getContent());
        existingComment.setPost(post);
        existingComment.setUser(user);

        Comment updatedComment = commentRepository.save(existingComment);
        return mapToDto(updatedComment);
    }

    @Override
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment", "id", id);
        }
        commentRepository.deleteById(id);
    }

    // Mapping helpers
    private CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private Comment mapToEntity(CommentDto commentDto) {
        return Comment.builder()
                .content(commentDto.getContent())
                .build();
    }
}
