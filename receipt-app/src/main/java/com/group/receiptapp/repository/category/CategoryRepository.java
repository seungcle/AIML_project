package com.group.receiptapp.repository.category;

import com.group.receiptapp.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // findById(Long id)는 JpaRepository에서 제공
}