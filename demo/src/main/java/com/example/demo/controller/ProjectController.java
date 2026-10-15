package com.example.demo.controller;

import com.example.demo.dto.project.ProjectRequest;
import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> getAll() { return projectService.getAll(); }

    @GetMapping("/{projectId}")
    public ProjectResponse getById(@PathVariable long projectId) { return projectService.getById(projectId); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@RequestBody ProjectRequest request) { return projectService.create(request); }

    @PutMapping("/{projectId}")
    public ProjectResponse update(@PathVariable long projectId, @RequestBody ProjectRequest request) {
        return projectService.update(projectId, request);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long projectId) { projectService.delete(projectId); }
}
