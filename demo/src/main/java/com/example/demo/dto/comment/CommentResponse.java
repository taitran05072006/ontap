package com.example.demo.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponse {
    private long id;
    private String content;
    private String taskTitle;
    private String userName;
}
