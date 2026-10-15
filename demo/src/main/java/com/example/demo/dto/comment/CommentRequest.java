package com.example.demo.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentRequest {
    private String content;
    private long task_id;
    private long user_id;
}
