package com.projectly.backend.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Comment {
    private String id = UUID.randomUUID().toString();
    private String text;
    private String author = "Me";
    private LocalDateTime createdAt = LocalDateTime.now();
}
