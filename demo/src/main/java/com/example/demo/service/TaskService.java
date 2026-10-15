package com.example.demo.service;

import com.example.demo.dto.task.TaskRequest;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private static final long DEFAULT_DUE_SECONDS = 6040000;

    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream().map(this::mapper).toList();
    }

    public TaskResponse getById(long task_id) {
        return mapper(taskRepository.findById(task_id)
                .orElseThrow(() -> new RuntimeException("khong tim thay task")));
    }

    public List<TaskResponse> getByUser(long user_id) {
        if (!userRepository.existsById(user_id)) throw new RuntimeException("khong ton tai user");
        return taskRepository.findByAssignee_Id(user_id).stream().map(this::mapper).toList();
    }

    public TaskResponse getByIdAndUser(long user_id, long task_id) {
        if (!userRepository.existsById(user_id)) throw new RuntimeException("khong tim tai user");
        Task task = taskRepository.findByIdAndAssignee_Id(task_id, user_id);
        if (task == null) throw new RuntimeException("khong tim thay task");
        return mapper(task);
    }

    public List<TaskResponse> getByStatus(String status) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.trim().toUpperCase());
        List<Task> tasks = taskRepository.findByStatus(taskStatus);
        return tasks.stream().map(this::mapper).toList();
    }

    public List<TaskResponse> getByPriority(String priority) {
        TaskPriority taskPriority = TaskPriority.valueOf(priority.trim().toUpperCase());
        List<Task> tasks = taskRepository.findByPriority(taskPriority);
        return tasks.stream().map(this::mapper).toList();
    }

    public List<TaskResponse> getByProject_id(long project_id) {
        if (!projectRepository.existsById(project_id)) throw new RuntimeException("khong tim thay project");
        List<Task> tasks = taskRepository.findByProject_Id(project_id);
        return tasks.stream().map(this::mapper).toList();
    }

    public TaskResponse create(TaskRequest request) {
        Project project = projectRepository.findById(request.getProject_id()).orElseThrow(()-> new RuntimeException("khong tim thay project"));
        Category category = categoryRepository.findById(request.getCategory_id()).orElseThrow(()-> new RuntimeException("khong tim thay category"));
        User user = userRepository.findById(request.getUser_id()).orElseThrow(() -> new RuntimeException("khong tim thay user"));
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(parseStatus(request.getStatus()));
        task.setPriority(parsePriority(request.getPriority()));
        task.setDueDate(request.getDueDate() != null ? request.getDueDate() : LocalDateTime.now().plusSeconds(DEFAULT_DUE_SECONDS));
        task.setCategory(category);
        task.setAssignee(user);
        task.setProject(project);

        return mapper(taskRepository.save(task));
    }

    public TaskResponse update(long task_id, TaskRequest request) {
        Project project = projectRepository.findById(request.getProject_id()).orElseThrow(()-> new RuntimeException("khong tim thay project"));
        Category category = categoryRepository.findById(request.getCategory_id()).orElseThrow(()-> new RuntimeException("khong tim thay category"));
        User user = userRepository.findById(request.getUser_id()).orElseThrow(() -> new RuntimeException("khong tim thay user"));
        Task task = taskRepository.findById(task_id).orElseThrow(() -> new RuntimeException("khong tim thay task"));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(parseStatus(request.getStatus()));
        task.setPriority(parsePriority(request.getPriority()));
        task.setDueDate(request.getDueDate() != null ? request.getDueDate() : task.getDueDate());
        task.setCategory(category);
        task.setAssignee(user);
        task.setProject(project);

        return mapper(taskRepository.save(task));
    }

    public void delete(long task_id) {
        Task task = taskRepository.findById(task_id).orElseThrow(() -> new RuntimeException("khong tim thasy task"));
        taskRepository.delete(task);
    }

    private TaskResponse mapper(Task task) {
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

    private TaskStatus parseStatus(String value) {
        return value == null || value.isBlank() ? TaskStatus.TODO : TaskStatus.valueOf(value.trim().toUpperCase());
    }

    private TaskPriority parsePriority(String value) {
        return value == null || value.isBlank() ? TaskPriority.MEDIUM : TaskPriority.valueOf(value.trim().toUpperCase());
    }
}
