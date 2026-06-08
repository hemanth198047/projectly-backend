package com.projectly.backend.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Step {
    private String id = UUID.randomUUID().toString();
    private String title;
    private boolean completed = false;
}
