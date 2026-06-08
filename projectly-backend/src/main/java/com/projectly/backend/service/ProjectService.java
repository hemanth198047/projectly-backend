package com.projectly.backend.service;

import com.projectly.backend.model.Project;
import com.projectly.backend.repository.GoalRepository;
import com.projectly.backend.repository.ProjectRepository;
import com.projectly.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;

    public List<Project> getAll() {
        return this.projectRepository.findAll();
    }

    public Project getById(String id) {
        return this.projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project create(Project project) {
        if (project.getDueDate() != null && project.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Due date cannot be in the past");
        }
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    public Project update(String id, Project updated) {
        Project existing = getById(id);
        if (updated.getDueDate() != null && updated.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Due date cannot be in the past");
        }
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setColor(updated.getColor());
        existing.setStatus(updated.getStatus());
        existing.setCategory(updated.getCategory());
        existing.setDueDate(updated.getDueDate());
        existing.setUpdatedAt(LocalDateTime.now());
        return this.projectRepository.save(existing);
    }

    public void delete(String id) {
        // Delete all tasks associated with this project
        taskRepository.findByProjectId(id).forEach(task -> taskRepository.deleteById(task.getId()));
        // Delete the project
        projectRepository.deleteById(id);
    }

    public List<Project> getSubProjects(String parentId) {
        return projectRepository.findByParentProjectId(parentId);
    }

    public List<Project> getRootProjects() {
        return projectRepository.findByParentProjectIdIsNull();
    }

    public int getProgress(String id) {
        var tasks = taskRepository.findByProjectId(id);
        var subProjects = projectRepository.findByParentProjectId(id);

        int totalTasks = tasks.size();
        long doneTasks = tasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();

        for (Project sub : subProjects) {
            var subTasks = taskRepository.findByProjectId(sub.getId());
            totalTasks += subTasks.size();
            doneTasks += subTasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();
        }

        if (totalTasks == 0) return 0;
        return (int) ((doneTasks * 100) / totalTasks);
    }

    public void syncGoalProgress(String projectId) {
        Project project = getById(projectId);
        if (project.getLinkedGoalId() == null) return;

        goalRepository.findById(project.getLinkedGoalId()).ifPresent(goal -> {
            int progress = getProgress(projectId);
            goal.setProgress(Math.min(100, Math.max(goal.getProgress(), progress)));
            if (goal.getProgress() == 100) goal.setStatus("ACHIEVED");
            goal.setUpdatedAt(LocalDateTime.now());
            goalRepository.save(goal);
        });
    }
}

