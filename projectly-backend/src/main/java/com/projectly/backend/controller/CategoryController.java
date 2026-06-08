package com.projectly.backend.controller;


import com.projectly.backend.model.Category;
import com.projectly.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Get All Categories",
            description = "Fetch All Categories information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories found"),
            @ApiResponse(responseCode = "404", description = "Categories not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Category> getAll() {
        return this.categoryService.getAll();
    }

    @PostMapping
    @Operation(
            summary = "Create a Category",
            description = "Create a category using category information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Category create(@RequestBody Category category) {
        return this.categoryService.create(category);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Category",
            description = "Update a category using category information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Category update(@PathVariable String id, @RequestBody Category category) {
        return this.categoryService.update(id, category);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Category",
            description = "Delete a category using category information in request body"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> delete(@PathVariable String id) {
        this.categoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Category deleted"));
    }
}
