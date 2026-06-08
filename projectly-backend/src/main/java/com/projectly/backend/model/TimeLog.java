package com.projectly.backend.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TimeLog {
    private String id = UUID.randomUUID().toString();
    private String description;
    private int minutes;
    private LocalDateTime loggedAt = LocalDateTime.now();
}
