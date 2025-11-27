package com.tomcvt.brickshop.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.exception.WrongOperationException;
import com.tomcvt.brickshop.model.Category;
import com.tomcvt.brickshop.repository.CategoryRepository;
import com.tomcvt.brickshop.utility.CategoryReferenceMap;

@Service
public class CategoryService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CategoryService.class);
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
        try {
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException e) {
            log.warn("Tried to delete category that is still referenced by products: {}", categoryName);
            throw new WrongOperationException("Cannot delete category that is still referenced by products");
        }
        categoryReferenceMap.remove(categoryName);
        return categoryName;
    }
}
