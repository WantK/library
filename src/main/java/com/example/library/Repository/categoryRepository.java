package com.example.library.Repository;

import com.example.library.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface categoryRepository extends JpaRepository<Category,Integer> {
    public Category findByCategory(String category);
}
