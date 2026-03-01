package com.example.mdbspringboot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String name;
    private String description;

    // Constructores, Getters y Setters
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}