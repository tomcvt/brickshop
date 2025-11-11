package com.tomcvt.brickshop.utility;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.model.Category;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.repository.CategoryRepository;
import com.tomcvt.brickshop.repository.ProductRepository;
import com.tomcvt.brickshop.service.CategoryService;

@Component
@Profile({"dev", "demo"})
public class CategoryExtractor {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private Map<String, Category> categoryCache;

    public CategoryExtractor(CategoryRepository categoryRepository, ProductRepository productRepository, 
            CategoryService categoryService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }
    
    @Transactional
    private void loadFromDescriptions() {
        Set<String> categoriesFromDescriptions = productRepository.findAllDescriptions().stream()
                .map(description -> description.split(" ")[0])
                .collect(Collectors.toCollection(HashSet::new));
        categoriesFromDescriptions.forEach(category -> {
            categoryService.addCategory(category);
        });
        categoryService.addCategory("Set");
        categoryService.addCategory("Minifigures");
    }

    @Transactional
    private void updateCategoriesForProducts() {
        categoryCache = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getName, c -> c));
        List<Product> products = productRepository.findAll();
        products.forEach(product -> {
            for (String word: product.getDescription().split(" ")) {
                if (categoryCache.containsKey(word)) {
                    product.addCategory(categoryCache.get(word));
                }
            }
            productRepository.save(product);
        });
    }
    @Transactional
    public void initCategories() {
        loadFromDescriptions();
        updateCategoriesForProducts();
    }
}
