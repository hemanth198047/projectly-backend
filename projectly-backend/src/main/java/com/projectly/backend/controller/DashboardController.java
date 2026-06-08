package com.projectly.backend.controller;

import com.projectly.backend.model.Category;
import com.projectly.backend.model.Goal;
import com.projectly.backend.model.Project;
import com.projectly.backend.model.Task;
import com.projectly.backend.repository.CategoryRepository;
import com.projectly.backend.repository.GoalRepository;
import com.projectly.backend.repository.ProjectRepository;
import com.projectly.backend.repository.TaskRepository;
import com.projectly.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final GoalRepository goalRepository;
    private final ProjectService projectService;
    private final CategoryRepository categoryRepository;

    private Map<String, Object> enrichTask(Task t, List<Project> projects,
                                           List<Category> categories) {
        Map<String, Object> tm = new HashMap<>();
        tm.put("id", t.getId());
        tm.put("title", t.getTitle());
        tm.put("status", t.getStatus());
        tm.put("priority", t.getPriority());
        tm.put("dueDate", t.getDueDate());
        tm.put("tags", t.getTags());
        tm.put("projectId", t.getProjectId());

        Project project = projects.stream()
                .filter(p -> p.getId().equals(t.getProjectId()))
                .findFirst().orElse(null);
        tm.put("projectName", project != null ? project.getName() : null);
        tm.put("projectColor", project != null ? project.getColor() : null);

        if (project != null && project.getCategoryId() != null) {
            categories.stream()
                    .filter(c -> c.getId().equals(project.getCategoryId()))
                    .findFirst()
                    .ifPresent(cat -> {
                        tm.put("categoryName", cat.getName());
                        tm.put("categoryIcon", cat.getIcon());
                        tm.put("categoryColor", cat.getColor());
                    });
        }
        return tm;
    }
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        List<Task> allTasks = taskRepository.findAll();
        List<Project> allProjects = projectRepository.findAll();
        List<Goal> allGoals = goalRepository.findAll();
        List<Category> allCategories = categoryRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

        List<Map<String, Object>> enrichedTasks = allTasks.stream()
                .map(t -> enrichTask(t, allProjects, allCategories)).toList();

        List<Map<String, Object>> todayTasks = enrichedTasks.stream()
                .filter(t -> t.get("dueDate") != null
                        && !((LocalDateTime) t.get("dueDate")).isBefore(startOfDay)
                        && !((LocalDateTime) t.get("dueDate")).isAfter(endOfDay))
                .toList();

        List<Map<String, Object>> overdueTasks = enrichedTasks.stream()
                .filter(t -> t.get("dueDate") != null
                        && ((LocalDateTime) t.get("dueDate")).isBefore(now)
                        && !"DONE".equals(t.get("status")))
                .toList();

        List<Map<String, Object>> inProgressTaskList = enrichedTasks.stream()
                .filter(t -> "IN_PROGRESS".equals(t.get("status"))).toList();

        List<Map<String, Object>> todoTaskList = enrichedTasks.stream()
                .filter(t -> "TODO".equals(t.get("status"))).toList();

        List<Map<String, Object>> doneTaskList = enrichedTasks.stream()
                .filter(t -> "DONE".equals(t.get("status"))).toList();

        List<Project> activeProjects = allProjects.stream()
                .filter(p -> "ACTIVE".equals(p.getStatus())).toList();

        List<Goal> activeGoals = allGoals.stream()
                .filter(g -> "ACTIVE".equals(g.getStatus())).toList();

        List<Goal> achievedGoals = allGoals.stream()
                .filter(g -> "ACHIEVED".equals(g.getStatus())).toList();

        List<Map<String, Object>> projectsWithProgress = allProjects.stream()
                .filter(p -> p.getParentProjectId() == null)
                .map(p -> {
                    Map<String, Object> pm = new HashMap<>();
                    pm.put("id", p.getId());
                    pm.put("name", p.getName());
                    pm.put("status", p.getStatus());
                    pm.put("color", p.getColor());
                    pm.put("dueDate", p.getDueDate());
                    pm.put("description", p.getDescription());
                    pm.put("progress", projectService.getProgress(p.getId()));
                    if (p.getCategoryId() != null) {
                        allCategories.stream()
                                .filter(c -> c.getId().equals(p.getCategoryId()))
                                .findFirst()
                                .ifPresent(cat -> {
                                    pm.put("categoryName", cat.getName());
                                    pm.put("categoryIcon", cat.getIcon());
                                    pm.put("categoryColor", cat.getColor());
                                });
                    }
                    List<Map<String, Object>> subProjects = allProjects.stream()
                            .filter(sp -> p.getId().equals(sp.getParentProjectId()))
                            .map(sp -> {
                                Map<String, Object> spm = new HashMap<>();
                                spm.put("id", sp.getId());
                                spm.put("name", sp.getName());
                                spm.put("status", sp.getStatus());
                                spm.put("color", sp.getColor());
                                spm.put("dueDate", sp.getDueDate());
                                spm.put("progress", projectService.getProgress(sp.getId()));
                                return spm;
                            }).toList();
                    pm.put("subProjects", subProjects);
                    return pm;
                }).toList();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProjects", allProjects.size());
        summary.put("activeProjects", activeProjects.size());
        summary.put("totalTasks", allTasks.size());
        summary.put("doneTasks", doneTaskList.size());
        summary.put("inProgressTasks", inProgressTaskList.size());
        summary.put("todoTasks", todoTaskList.size());
        summary.put("totalGoals", allGoals.size());
        summary.put("activeGoals", activeGoals.size());
        summary.put("achievedGoals", achievedGoals.size());
        summary.put("todayTasks", todayTasks);
        summary.put("overdueTasks", overdueTasks);
        summary.put("inProgressTaskList", inProgressTaskList);
        summary.put("todoTaskList", todoTaskList);
        summary.put("doneTaskList", doneTaskList);
        summary.put("projectsWithProgress", projectsWithProgress);
        summary.put("activeGoalList", activeGoals);

        // Completion by category
        Map<String, Map<String, Object>> categoryStats = new HashMap<>();
        allCategories.forEach(cat -> {
            List<Project> catProjects = allProjects.stream()
                    .filter(p -> cat.getId().equals(p.getCategoryId())).toList();
            long catTotal = catProjects.stream()
                    .mapToLong(p -> taskRepository.findByProjectId(p.getId()).size()).sum();
            long catDone = catProjects.stream()
                    .mapToLong(p -> taskRepository.findByProjectId(p.getId()).stream()
                            .filter(t -> "DONE".equals(t.getStatus())).count()).sum();
            Map<String, Object> cs = new HashMap<>();
            cs.put("categoryId", cat.getId());
            cs.put("categoryName", cat.getName());
            cs.put("categoryIcon", cat.getIcon());
            cs.put("categoryColor", cat.getColor());
            cs.put("totalProjects", catProjects.size());
            cs.put("totalTasks", catTotal);
            cs.put("doneTasks", catDone);
            cs.put("completionRate", catTotal > 0 ? (int)((catDone * 100) / catTotal) : 0);
            categoryStats.put(cat.getId(), cs);
        });
        summary.put("categoryStats", categoryStats.values());

        List<Project> overdueProjects = allProjects.stream()
                .filter(p -> p.getDueDate() != null
                        && p.getDueDate().isBefore(now)
                        && !"DONE".equals(p.getStatus()))
                .toList();

        List<Goal> overdueGoals = allGoals.stream()
                .filter(g -> g.getTargetDate() != null
                        && g.getTargetDate().isBefore(now)
                        && !"ACHIEVED".equals(g.getStatus()))
                .toList();

        summary.put("overdueProjects", overdueProjects);
        summary.put("overdueGoals", overdueGoals);
        return summary;
    }
}
