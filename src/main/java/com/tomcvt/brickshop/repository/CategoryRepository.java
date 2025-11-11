package com.tomcvt.brickshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomcvt.brickshop.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
