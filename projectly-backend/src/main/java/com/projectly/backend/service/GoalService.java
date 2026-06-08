package com.projectly.backend.service;


import com.projectly.backend.model.Goal;
import com.projectly.backend.model.Step;
import com.projectly.backend.model.SubGoal;
import com.projectly.backend.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<Goal> getAll() {
        return this.goalRepository.findAll();
    }

    public Goal getById(String id) {
        return this.goalRepository.findById(id).orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    public Goal create(Goal goal) {
        goal.setCreatedAt(LocalDateTime.now());
        goal.setUpdatedAt(LocalDateTime.now());
        return this.goalRepository.save(goal);
    }

    public Goal update(String id, Goal updated) {
        Goal existing = getById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setTargetDate(updated.getTargetDate());
        existing.setStatus(updated.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return this.goalRepository.save(existing);
    }

    public void delete(String id) {
        this.goalRepository.deleteById(id);
    }

    public Goal updateProgress(String id, int progress) {
        Goal existing = getById(id);
        existing.setProgress(Math.min(100, Math.max(0, progress)));
        if (existing.getProgress() == 100) existing.setStatus("ACHIEVED");
        existing.setUpdatedAt(LocalDateTime.now());
        return this.goalRepository.save(existing);
    }

    public Goal addSubGoal(String id, SubGoal subGoal) {
        Goal goal = getById(id);
        if (goal.getSubGoals() == null) goal.setSubGoals(new java.util.ArrayList<>());
        goal.getSubGoals().add(subGoal);
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    public Goal updateSubGoal(String id, String subGoalId, SubGoal updated) {
        Goal goal = getById(id);
        goal.getSubGoals().stream()
                .filter(sg -> sg.getId().equals(subGoalId))
                .findFirst()
                .ifPresent(sg -> {
                    sg.setTitle(updated.getTitle());
                    sg.setDescription(updated.getDescription());
                    sg.setStatus(updated.getStatus());
                });
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    public Goal deleteSubGoal(String id, String subGoalId) {
        Goal goal = getById(id);
        goal.getSubGoals().removeIf(sg -> sg.getId().equals(subGoalId));
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    public Goal addStep(String id, String subGoalId, Step step) {
        Goal goal = getById(id);
        goal.getSubGoals().stream()
                .filter(sg -> sg.getId().equals(subGoalId))
                .findFirst()
                .ifPresent(sg -> {
                    if (sg.getSteps() == null) sg.setSteps(new java.util.ArrayList<>());
                    sg.getSteps().add(step);
                });
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    public Goal toggleStep(String id, String subGoalId, String stepId) {
        Goal goal = getById(id);
        goal.getSubGoals().stream()
                .filter(sg -> sg.getId().equals(subGoalId))
                .findFirst()
                .ifPresent(sg -> sg.getSteps().stream()
                        .filter(s -> s.getId().equals(stepId))
                        .findFirst()
                        .ifPresent(s -> s.setCompleted(!s.isCompleted())));
        updateGoalProgressFromSubGoals(goal);
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    public Goal deleteStep(String id, String subGoalId, String stepId) {
        Goal goal = getById(id);
        goal.getSubGoals().stream()
                .filter(sg -> sg.getId().equals(subGoalId))
                .findFirst()
                .ifPresent(sg -> sg.getSteps().removeIf(s -> s.getId().equals(stepId)));
        updateGoalProgressFromSubGoals(goal);
        goal.setUpdatedAt(LocalDateTime.now());
        return goalRepository.save(goal);
    }

    private void updateGoalProgressFromSubGoals(Goal goal) {
        if (goal.getSubGoals() == null || goal.getSubGoals().isEmpty()) return;
        int total = goal.getSubGoals().stream()
                .mapToInt(sg -> sg.getSteps() == null ? 0 : sg.getSteps().size())
                .sum();
        if (total == 0) return;
        long done = goal.getSubGoals().stream()
                .mapToLong(sg -> sg.getSteps() == null ? 0 :
                        sg.getSteps().stream().filter(Step::isCompleted).count())
                .sum();
        goal.setProgress((int) ((done * 100) / total));
        if (goal.getProgress() == 100) goal.setStatus("ACHIEVED");
    }
}

