package com.example.demo.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskRequest {
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime dueDate;
    private long project_id;
    private long category_id;
    private long user_id;

}
