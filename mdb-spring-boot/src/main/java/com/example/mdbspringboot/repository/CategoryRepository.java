package com.example.mdbspringboot.repository;

import com.example.mdbspringboot.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
}