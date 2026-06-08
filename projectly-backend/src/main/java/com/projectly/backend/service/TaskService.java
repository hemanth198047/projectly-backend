package com.projectly.backend.service;

import com.projectly.backend.model.Comment;
import com.projectly.backend.model.Task;
import com.projectly.backend.model.TimeLog;
import com.projectly.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    public List<Task> getByProjectId(String projectId) {
        return this.taskRepository.findByProjectId(projectId);
    }

    public List<Task> getAll() {
        return this.taskRepository.findAll();
    }

    public List<Task> getByStatus(String status) {
        return this.taskRepository.findByStatus(status);
    }

    public List<Task> search(String keyword) {
        return this.taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Task getById(String id) {
        return this.taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public Task create(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return this.taskRepository.save(task);
    }

    public Task update(String id, Task updated) {
        Task existingTask = getById(id);
        existingTask.setTitle(updated.getTitle());
        existingTask.setDescription(updated.getDescription());
        existingTask.setStatus(updated.getStatus());
        existingTask.setPriority(updated.getPriority());
        existingTask.setDueDate(updated.getDueDate());
        existingTask.setTags(updated.getTags());
        existingTask.setUpdatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(existingTask);
        if (saved.getProjectId() != null) {
            projectService.syncGoalProgress(saved.getProjectId());
        }
        return saved;
    }

    public void delete(String id) {
        this.taskRepository.deleteById(id);
    }

    public void processRecurringTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> doneTasks = taskRepository.findByStatus("DONE");

        for (Task task : doneTasks) {
            if (task.getRecurrence() == null || "NONE".equals(task.getRecurrence())) continue;

            LocalDateTime nextDue = null;
            LocalDateTime base = task.getDueDate() != null ? task.getDueDate() : now;

            switch (task.getRecurrence()) {
                case "DAILY" -> nextDue = base.plusDays(1);
                case "WEEKLY" -> nextDue = base.plusWeeks(1);
                case "MONTHLY" -> nextDue = base.plusMonths(1);
            }

            if (nextDue != null && nextDue.isBefore(now)) {
                Task newTask = new Task();
                newTask.setTitle(task.getTitle());
                newTask.setDescription(task.getDescription());
                newTask.setProjectId(task.getProjectId());
                newTask.setPriority(task.getPriority());
                newTask.setTags(task.getTags());
                newTask.setRecurrence(task.getRecurrence());
                newTask.setStatus("TODO");
                newTask.setDueDate(nextDue);
                newTask.setCreatedAt(now);
                newTask.setUpdatedAt(now);
                taskRepository.save(newTask);

                task.setRecurrence("NONE");
                task.setUpdatedAt(now);
                taskRepository.save(task);
            }
        }
    }

    public Task addComment(String id, Comment comment) {
        Task task = getById(id);
        if (task.getComments() == null) task.setComments(new java.util.ArrayList<>());
        task.getComments().add(comment);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task deleteComment(String id, String commentId) {
        Task task = getById(id);
        task.getComments().removeIf(c -> c.getId().equals(commentId));
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task addTimeLog(String id, TimeLog timeLog) {
        Task task = getById(id);
        if (task.getTimeLogs() == null) task.setTimeLogs(new java.util.ArrayList<>());
        task.getTimeLogs().add(timeLog);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task deleteTimeLog(String id, String timeLogId) {
        Task task = getById(id);
        task.getTimeLogs().removeIf(t -> t.getId().equals(timeLogId));
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public int getTotalMinutes(String id) {
        Task task = getById(id);
        if (task.getTimeLogs() == null) return 0;
        return task.getTimeLogs().stream().mapToInt(TimeLog::getMinutes).sum();
    }
}

