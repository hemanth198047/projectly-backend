package com.projectly.backend.repository;


import com.projectly.backend.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GoalRepository extends MongoRepository<Goal, String> {
    List<Goal> findByTitleContainingIgnoreCase(String title);
}

