package com.example.demo.controller;

import com.example.demo.dto.task.TaskRequest;
import com.example.demo.dto.task.TaskResponse;
import com.example.demo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> getAll() { return taskService.getAll(); }

    @GetMapping("/{taskId}")
    public TaskResponse getById(@PathVariable long taskId) { return taskService.getById(taskId); }

    @GetMapping("/user/{userId}")
    public List<TaskResponse> getByUser(@PathVariable long userId) { return taskService.getByUser(userId); }

    @GetMapping("/status/{status}")
    public List<TaskResponse> getByStatus(@PathVariable String status) { return taskService.getByStatus(status); }

    @GetMapping("/priority/{priority}")
    public List<TaskResponse> getByPriority(@PathVariable String priority) { return taskService.getByPriority(priority); }

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getByProject(@PathVariable long projectId) { return taskService.getByProject_id(projectId); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@RequestBody TaskRequest request) { return taskService.create(request); }

    @PutMapping("/{taskId}")
    public TaskResponse update(@PathVariable long taskId, @RequestBody TaskRequest request) {
        return taskService.update(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long taskId) { taskService.delete(taskId); }

}
