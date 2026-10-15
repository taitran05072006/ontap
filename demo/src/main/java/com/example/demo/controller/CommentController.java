package com.example.demo.controller;

import com.example.demo.dto.comment.CommentRequest;
import com.example.demo.dto.comment.CommentResponse;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponse> getAll() { return commentService.getAll(); }

    @GetMapping("/{commentId}")
    public CommentResponse getById(@PathVariable long commentId) { return commentService.getById(commentId); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@RequestBody CommentRequest request) { return commentService.create(request); }

    @PutMapping("/{commentId}")
    public CommentResponse update(@PathVariable long commentId, @RequestBody CommentRequest request) {
        return commentService.update(commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId) { commentService.delete(commentId); }
}
