package com.example.demo.service;

import com.example.demo.dto.project.ProjectRequest;
import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectStatus;
import com.example.demo.entity.Task;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public List<ProjectResponse> getAll() {
        return projectRepository.findAll().stream().map(this::mapper).toList();
    }

    public ProjectResponse getById(long project_id) {
        Project project = projectRepository.findById(project_id).orElseThrow(() -> new RuntimeException("khong tim thay project"));
        return mapper(project);
    }

    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now());
        project.setEndDate(request.getEndDate());
        project.setStatus(parseStatus(request.getStatus()));
        return mapper(projectRepository.save(project));
    }

    public ProjectResponse update(long project_id, ProjectRequest request) {
        Project project = projectRepository.findById(project_id).orElseThrow(() -> new RuntimeException("khong tim thay project"));
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate() != null ? request.getStartDate() : project.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(parseStatus(request.getStatus()));
        return mapper(projectRepository.save(project));
    }

    public void delete(long project_id) {
        Project project = projectRepository.findById(project_id).orElseThrow(() -> new RuntimeException("khong tim thay project"));
        projectRepository.delete(project);
    }

    private ProjectResponse mapper(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus().name())
                .tasks(project.getTasks().stream().map(this::taskMapper).toList())
                .build();
    }

    private ProjectStatus parseStatus(String status) {
        return status == null || status.isBlank()
                ? ProjectStatus.PLANNING
                : ProjectStatus.valueOf(status.trim().toUpperCase());
    }

    private TaskResponse taskMapper(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .dueDate(task.getDueDate())
                .projectName(task.getProject() == null ? null : task.getProject().getName())
                .projectId(task.getProject() == null ? 0 : task.getProject().getId())
                .categoryName(task.getCategory() == null ? null : task.getCategory().getName())
                .categoryId(task.getCategory() == null ? 0 : task.getCategory().getId())
                .userName(task.getAssignee() == null ? null : task.getAssignee().getUsername())
                .userId(task.getAssignee() == null ? 0 : task.getAssignee().getId())
                .build();
    }
}
