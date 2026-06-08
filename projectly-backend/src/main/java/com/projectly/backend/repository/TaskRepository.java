package com.projectly.backend.repository;

import com.projectly.backend.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByProjectId(String projectId);
    List<Task> findByStatus(String status);
    List<Task> findByPriority(String priority);
    List<Task> findByTitleContainingIgnoreCase(String title);
}

