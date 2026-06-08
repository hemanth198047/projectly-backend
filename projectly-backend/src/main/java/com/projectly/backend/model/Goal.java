package com.projectly.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "goals")
public class Goal {
    @Id
    private String id;
    private String title;
    private String description;
    private int progress = 0; // 0 to 100
    private String status = "ACTIVE"; // ACTIVE, ACHIEVED
    private LocalDateTime targetDate;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private List<SubGoal> subGoals = new ArrayList<>();
}

