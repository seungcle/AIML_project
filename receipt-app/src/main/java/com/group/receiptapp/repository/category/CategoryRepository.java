package com.group.receiptapp.repository.category;

import com.group.receiptapp.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // categoryname->categoryid
    Optional<Category> findByName(String name);
}