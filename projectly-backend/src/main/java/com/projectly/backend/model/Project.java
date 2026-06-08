package com.projectly.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "projects")
public class Project {
    @Id
    private String id;
    private String name;
    private String description;
    private String color;
    private String category = "General";
    private String categoryId;
    private String linkedGoalId;
    private String parentProjectId;
    private String status = "ACTIVE"; // ACTIVE, IN PROGRESS, PAUSED, DONE
    private LocalDateTime dueDate;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

