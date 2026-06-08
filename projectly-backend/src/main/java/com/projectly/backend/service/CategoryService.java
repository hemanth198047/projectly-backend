package com.projectly.backend.service;

import com.projectly.backend.model.Category;
import com.projectly.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return this.categoryRepository.findAll();
    }

    public Category create(Category category) {
        if (this.categoryRepository.existsByNameIgnoreCase(category.getName()))
            throw new RuntimeException("Category already exists");
        return this.categoryRepository.save(category);
    }

    public Category update(String id, Category updated) {
        Category existing = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(updated.getName());
        existing.setColor(updated.getColor());
        existing.setIcon(updated.getIcon());
        return this.categoryRepository.save(existing);
    }

    public void delete(String id) {
        this.categoryRepository.deleteById(id);
    }
}
