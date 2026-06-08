package com.projectly.backend.service;


import com.projectly.backend.model.Goal;
import com.projectly.backend.model.Project;
import com.projectly.backend.model.Task;
import com.projectly.backend.repository.GoalRepository;
import com.projectly.backend.repository.ProjectRepository;
import com.projectly.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final GoalRepository goalRepository;
    private final EmailService emailService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public void sendDailyReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

        // Overdue tasks
        List<Task> overdue = taskRepository.findAll().stream()
                .filter(t -> t.getDueDate() != null
                        && t.getDueDate().isBefore(now)
                        && !"DONE".equals(t.getStatus()))
                .collect(Collectors.toList());

        // Today tasks
        List<Task> today = taskRepository.findAll().stream()
                .filter(t -> t.getDueDate() != null
                        && !t.getDueDate().isBefore(startOfDay)
                        && !t.getDueDate().isAfter(endOfDay))
                .collect(Collectors.toList());

        // Due tomorrow
        List<Task> dueTomorrow = taskRepository.findAll().stream()
                .filter(t -> t.getDueDate() != null
                        && t.getDueDate().isAfter(endOfDay)
                        && t.getDueDate().isBefore(tomorrow.toLocalDate().atTime(23, 59, 59))
                        && !"DONE".equals(t.getStatus()))
                .collect(Collectors.toList());

        // Overdue projects
        List<Project> overdueProjects = projectRepository.findAll().stream()
                .filter(p -> p.getDueDate() != null
                        && p.getDueDate().isBefore(now)
                        && !"DONE".equals(p.getStatus()))
                .collect(Collectors.toList());

        // Overdue goals
        List<Goal> overdueGoals = goalRepository.findAll().stream()
                .filter(g -> g.getTargetDate() != null
                        && g.getTargetDate().isBefore(now)
                        && !"ACHIEVED".equals(g.getStatus()))
                .collect(Collectors.toList());

        if (overdue.isEmpty() && today.isEmpty() && dueTomorrow.isEmpty()
                && overdueProjects.isEmpty() && overdueGoals.isEmpty()) return;

        StringBuilder body = new StringBuilder();
        body.append("ProjectsNGoals - Daily Reminder\n");
        body.append("=".repeat(40)).append("\n\n");
        body.append("Date: ").append(now.format(FMT)).append("\n\n");

        if (!overdue.isEmpty()) {
            body.append("⚠️ OVERDUE TASKS (").append(overdue.size()).append(")\n");
            body.append("-".repeat(30)).append("\n");
            overdue.forEach(t -> body.append("• ").append(t.getTitle())
                    .append(" [").append(t.getPriority()).append("]")
                    .append(" - Due: ").append(t.getDueDate().format(FMT)).append("\n"));
            body.append("\n");
        }

        if (!today.isEmpty()) {
            body.append("📅 DUE TODAY (").append(today.size()).append(")\n");
            body.append("-".repeat(30)).append("\n");
            today.forEach(t -> body.append("• ").append(t.getTitle())
                    .append(" [").append(t.getStatus()).append("]").append("\n"));
            body.append("\n");
        }

        if (!dueTomorrow.isEmpty()) {
            body.append("🔔 DUE TOMORROW (").append(dueTomorrow.size()).append(")\n");
            body.append("-".repeat(30)).append("\n");
            dueTomorrow.forEach(t -> body.append("• ").append(t.getTitle())
                    .append(" [").append(t.getPriority()).append("]").append("\n"));
            body.append("\n");
        }

        if (!overdueProjects.isEmpty()) {
            body.append("📁 OVERDUE PROJECTS (").append(overdueProjects.size()).append(")\n");
            body.append("-".repeat(30)).append("\n");
            overdueProjects.forEach(p -> body.append("• ").append(p.getName())
                    .append(" - Due: ").append(p.getDueDate().format(FMT)).append("\n"));
            body.append("\n");
        }

        if (!overdueGoals.isEmpty()) {
            body.append("🎯 OVERDUE GOALS (").append(overdueGoals.size()).append(")\n");
            body.append("-".repeat(30)).append("\n");
            overdueGoals.forEach(g -> body.append("• ").append(g.getTitle())
                    .append(" - Target: ").append(g.getTargetDate().format(FMT)).append("\n"));
            body.append("\n");
        }

        body.append("=".repeat(40)).append("\n");
        body.append("Visit your dashboard: http://localhost:5173\n");

        emailService.sendReminder("📋 ProjectsNGoals Daily Reminder - "
                + now.toLocalDate(), body.toString());
    }
}
