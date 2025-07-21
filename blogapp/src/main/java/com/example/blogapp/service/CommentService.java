package com.example.blogapp.service;

import com.example.blogapp.dto.CommentDto;
import java.util.List;

public interface CommentService {

    List<CommentDto> getAllComments();
    CommentDto getCommentById(Long id);
    CommentDto createComment(CommentDto commentDto);
    CommentDto updateComment(Long id, CommentDto commentDto);
    void deleteComment(Long id);
}
