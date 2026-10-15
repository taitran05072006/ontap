package com.example.demo.repository;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByProject_Id(long projectId);

    List<Task> findByCategory_Id(long categoryId);

    List<Task> findByAssignee_Id(long assigneeId);

    Task findByIdAndAssignee_Id(long id, long assigneeId);

    List<Task> findByTitleContainingIgnoreCase(String title);
}
