package com.projectly.backend.controller;

import com.projectly.backend.model.Comment;
import com.projectly.backend.model.Task;
import com.projectly.backend.model.TimeLog;
import com.projectly.backend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks APIs", description = "Operations related to Projects")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @Operation(
            summary = "Get All Tasks",
            description = "Fetch All Tasks information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Task> getAll(@RequestParam(required = false) String projectId,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String search) {
        if (search != null) {
            return taskService.search(search);
        } else if (status != null) {
            return taskService.getByStatus(status);
        } else if (projectId != null) {
            return taskService.getByProjectId(projectId);
        }
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Task by ID",
            description = "Fetch task information using task ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found"),
            @ApiResponse(responseCode = "404", description = "Tasks not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Task getById(@PathVariable String id) {
        return taskService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create a Task",
            description = "Create Task using task information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Created"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Task by ID",
            description = "Update task information using task ID and updated task information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Updated"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Task update(@PathVariable String id, @RequestBody Task task) {
        return taskService.update(id, task);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Task by ID",
            description = "Delete task information using task ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> delete(@PathVariable String id) {
        taskService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }

    @PostMapping("/{id}/comments")
    public Task addComment(@PathVariable String id, @RequestBody Comment comment) {
        return taskService.addComment(id, comment);
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public Task deleteComment(@PathVariable String id, @PathVariable String commentId) {
        return taskService.deleteComment(id, commentId);
    }

    @PostMapping("/{id}/timelogs")
    public Task addTimeLog(@PathVariable String id,
                           @RequestBody TimeLog timeLog) {
        return taskService.addTimeLog(id, timeLog);
    }

    @DeleteMapping("/{id}/timelogs/{timeLogId}")
    public Task deleteTimeLog(@PathVariable String id,
                              @PathVariable String timeLogId) {
        return taskService.deleteTimeLog(id, timeLogId);
    }

    @GetMapping("/{id}/timelogs/total")
    public Map<String, Integer> getTotalTime(@PathVariable String id) {
        return Map.of("totalMinutes", taskService.getTotalMinutes(id));
    }
}


