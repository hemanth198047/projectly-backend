package com.projectly.backend.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class SubGoal {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private String status = "ACTIVE";
    private List<Step> steps = new ArrayList<>();

    public int getProgress() {
        if (steps == null || steps.isEmpty()) return 0;
        long done = steps.stream().filter(Step::isCompleted).count();
        return (int) ((done * 100) / steps.size());
    }
}
