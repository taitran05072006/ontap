package com.example.demo.dto.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TaskResponse {
    private long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private String projectName;
    private long projectId;
    private String categoryName;
    private long categoryId;
    private String userName;
    private long userId;
}
