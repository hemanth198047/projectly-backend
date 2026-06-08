package com.projectly.backend.controller;

import com.projectly.backend.model.Project;
import com.projectly.backend.service.ProjectService;
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
@RequestMapping("/api/projects")
@Tag(name = "Projects APIs", description = "Operations related to Projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    @Operation(
            summary = "Get All Project",
            description = "Fetch All Projects information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects found"),
            @ApiResponse(responseCode = "404", description = "projects not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Project by ID",
            description = "Fetch project information using project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects found"),
            @ApiResponse(responseCode = "404", description = "Projects not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Project getById(@PathVariable String id) {
        return projectService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create a Project",
            description = "Create project using project information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project Created"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Project create(@RequestBody Project project) {
        return projectService.create(project);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Project by ID",
            description = "Update project information using project ID and updated project information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project Updated"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Project update(@PathVariable String id, @RequestBody Project project) {
        return projectService.update(id, project);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Project by ID",
            description = "Delete project information using project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project Deleted"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> delete(@PathVariable String id) {
        projectService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Project deleted successfully"));
    }

    @GetMapping("/{id}/progress")
    @Operation(
            summary = "Get Project Progress by ID",
            description = "Fetch project progress information using project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getProgress(@PathVariable String id) {
        return ResponseEntity.ok(Map.of("progress", projectService.getProgress(id)));
    }

    @GetMapping("/{id}/subprojects")
    @Operation(
            summary = "Get Sub Project for a given Project ID",
            description = "Fetch project progress information using project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Project> getSubProjects(@PathVariable String id) {
        return projectService.getSubProjects(id);
    }

    @GetMapping("/roots")
    @Operation(
            summary = "Get Root Projects Progress by ID",
            description = "Fetch project progress information using project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Project> getRootProjects() {
        return projectService.getRootProjects();
    }
}


