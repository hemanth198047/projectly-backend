package com.projectly.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    private String title;
    private String description;
    private String projectId;
    private String status= "TODO"; // TODO, IN_PROGRESS, DONE
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH
    private LocalDateTime dueDate;
    private List<String> tags;
    private String recurrence; // NONE, DAILY, WEEKLY, MONTHLY
    private List<Comment> comments = new java.util.ArrayList<>();
    private List<TimeLog> timeLogs = new java.util.ArrayList<>();
    private LocalDateTime lastRecurred;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

