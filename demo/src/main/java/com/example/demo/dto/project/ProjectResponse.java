package com.example.demo.dto.project;

import com.example.demo.dto.task.TaskResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ProjectResponse {
    private long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private List<TaskResponse> tasks;
}
