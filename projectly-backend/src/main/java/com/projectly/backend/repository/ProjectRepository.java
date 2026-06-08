package com.projectly.backend.repository;

import com.projectly.backend.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByParentProjectId(String parentProjectId);
    List<Project> findByParentProjectIdIsNull();
    List<Project> findByNameContainingIgnoreCase(String name);
}

