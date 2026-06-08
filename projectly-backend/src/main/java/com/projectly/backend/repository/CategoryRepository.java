package com.projectly.backend.repository;


import com.projectly.backend.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
    boolean existsByNameIgnoreCase(String name);
}
