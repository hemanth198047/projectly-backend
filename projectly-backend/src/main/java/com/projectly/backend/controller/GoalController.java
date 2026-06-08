package com.projectly.backend.controller;

import com.projectly.backend.model.Goal;
import com.projectly.backend.model.Step;
import com.projectly.backend.model.SubGoal;
import com.projectly.backend.service.GoalService;
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
@RequestMapping("/api/goals")
@Tag(name = "Goals APIs", description = "Operations related to Goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    @Operation(
            summary = "Get All Goals",
            description = "Fetch All Goals information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals found"),
            @ApiResponse(responseCode = "404", description = "Goals not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Goal> getAll() {
        return goalService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Goal by ID",
            description = "Fetch Goal information using Goal ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals found"),
            @ApiResponse(responseCode = "404", description = "Goals not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Goal getById(@PathVariable String id) {
        return goalService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create a Goal",
            description = "Create Goal using project information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal Created"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Goal create(@RequestBody Goal goal) {
        return goalService.create(goal);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Goal by ID",
            description = "Update Goal information using project ID and updated Goal information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal Updated"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Goal update(@PathVariable String id, @RequestBody Goal goal) {
        return goalService.update(id, goal);
    }

    @PatchMapping("/{id}/progress")
    @Operation(
            summary = "Patch Goal progress by ID",
            description = "Patch Goal information using Goal ID and updated Goal information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal Patched"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Goal updateProgress(@PathVariable String id, @RequestBody Map<String, Integer> body) {
        return goalService.updateProgress(id, body.get("progress"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Goal by ID",
            description = "Delete Goal information using Goal ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal Deleted"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> delete(@PathVariable String id) {
        goalService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }

    // Sub-goal endpoints
    @PostMapping("/{id}/subgoals")
    public Goal addSubGoal(@PathVariable String id, @RequestBody SubGoal subGoal) {
        return goalService.addSubGoal(id, subGoal);
    }

    @PutMapping("/{id}/subgoals/{subGoalId}")
    public Goal updateSubGoal(@PathVariable String id, @PathVariable String subGoalId,
                              @RequestBody SubGoal updated) {
        return goalService.updateSubGoal(id, subGoalId, updated);
    }

    @DeleteMapping("/{id}/subgoals/{subGoalId}")
    public Goal deleteSubGoal(@PathVariable String id, @PathVariable String subGoalId) {
        return goalService.deleteSubGoal(id, subGoalId);
    }

    // Step endpoints
    @PostMapping("/{id}/subgoals/{subGoalId}/steps")
    public Goal addStep(@PathVariable String id, @PathVariable String subGoalId,
                        @RequestBody Step step) {
        return goalService.addStep(id, subGoalId, step);
    }

    @PatchMapping("/{id}/subgoals/{subGoalId}/steps/{stepId}/toggle")
    public Goal toggleStep(@PathVariable String id, @PathVariable String subGoalId,
                           @PathVariable String stepId) {
        return goalService.toggleStep(id, subGoalId, stepId);
    }

    @DeleteMapping("/{id}/subgoals/{subGoalId}/steps/{stepId}")
    public Goal deleteStep(@PathVariable String id, @PathVariable String subGoalId,
                           @PathVariable String stepId) {
        return goalService.deleteStep(id, subGoalId, stepId);
    }
}


