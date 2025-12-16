package com.tomcvt.brickshop.utility;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.model.Category;
import com.tomcvt.brickshop.repository.CategoryRepository;

@Service
public class CategoryReferenceMap {
    private Map<String, Long> map;
    private final CategoryRepository categoryRepository;

    public CategoryReferenceMap(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.map = new ConcurrentHashMap<>();
    }

    public Long put(String name, Long id) {
        Long existingId = map.get(name);
        map.put(name, id);
        return existingId;
    }

    public Long remove(String name) {
        return map.remove(name);
    }

    public Long get(String name) {
        return map.get(name);
    }

    public Map<String, Long> getMap() {
        return map;
    }

    public void setMap(Map<String, Long> map) {
        this.map = map;
    }

    public void initMap() {
        List<Category> categories = categoryRepository.findAll();
        categories.forEach(category -> {
            map.put(category.getName(), category.getId());
        });
    }

    public Set<Long> getIds(Set<String> names) {
        return names.stream()
                .map(map::get)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    public List<Long> getIdsList(List<String> names) {
        return names.stream()
                .map(map::get)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }
}
