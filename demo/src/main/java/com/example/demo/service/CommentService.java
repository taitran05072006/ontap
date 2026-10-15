package com.example.demo.service;

import com.example.demo.dto.comment.CommentRequest;
import com.example.demo.dto.comment.CommentResponse;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<CommentResponse> getAll() {
        return commentRepository.findAll().stream().map(this::mapper).toList();
    }

    public CommentResponse getById(long comment_id) {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new RuntimeException("khong tim thay comment"));
        return mapper(comment);
    }

    public CommentResponse create(CommentRequest request) {
        Task task = taskRepository.findById(request.getTask_id()).orElseThrow(() -> new RuntimeException("khong tim thay task"));
        User user = userRepository.findById(request.getUser_id()).orElseThrow(() -> new RuntimeException("khong tim thay user"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setUser(user);
        return mapper(commentRepository.save(comment));
    }

    public CommentResponse update(long comment_id, CommentRequest request) {
        Task task = taskRepository.findById(request.getTask_id()).orElseThrow(() -> new RuntimeException("khong tim thay task"));
        User user = userRepository.findById(request.getUser_id()).orElseThrow(() -> new RuntimeException("khong tim thay user"));

        Comment comment  = commentRepository.findById(comment_id).orElseThrow(() -> new RuntimeException("khong tim thay comment"));
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setUser(user);
        return mapper(commentRepository.save(comment));
    }

    public void delete(long comment_id) {
        Comment comment  = commentRepository.findById(comment_id).orElseThrow(() -> new RuntimeException("khong tim thay comment"));
        commentRepository.delete(comment);
    }

    private CommentResponse mapper(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .taskTitle(comment.getTask().getTitle())
                .userName(comment.getUser().getUsername())
                .build();
    }
}
