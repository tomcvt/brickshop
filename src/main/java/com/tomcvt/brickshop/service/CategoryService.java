package com.tomcvt.brickshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.model.Category;
import com.tomcvt.brickshop.repository.CategoryRepository;
import com.tomcvt.brickshop.utility.CategoryReferenceMap;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;
    private CategoryReferenceMap categoryReferenceMap;

    public CategoryService(CategoryRepository categoryRepository,  
    CategoryReferenceMap categoryReferenceMap) {
        this.categoryRepository = categoryRepository;
        this.categoryReferenceMap = categoryReferenceMap;
    }

    public List<String> getCategoriesNames() {
        return categoryRepository.findAll().stream().map(Category::getName).toList();
    }
    public String addCategory(String categoryName) {
        if (categoryRepository.findByName(categoryName).isPresent()) {
            throw new IllegalArgumentException("Category with that name already exists");
        }
        Category category = new Category(categoryName, categoryName);
        category = categoryRepository.save(category);
        categoryReferenceMap.put(categoryName, category.getId());
        return categoryName;
    }
    public String deleteCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("No such category"));
        categoryRepository.delete(category);
        categoryReferenceMap.remove(categoryName);
        return categoryName;
    }
}
