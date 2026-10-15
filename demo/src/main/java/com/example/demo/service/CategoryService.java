package com.example.demo.service;

import com.example.demo.dto.category.CategoryRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryResponse getById(long category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("khong timi thay category"));
        return mapper(category);
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::mapper).toList();
    }

    public CategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(() -> new RuntimeException("khong tim thasy name category"));
        return mapper(category);

    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapper(categoryRepository.save(category));
    }

    public CategoryResponse update(long category_id, CategoryRequest request) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("khong tim thay category"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapper(categoryRepository.save(category));
    }

    public void delete(long category_id) {
        Category category = categoryRepository.findById(category_id).orElseThrow(() -> new RuntimeException("khong tim thay category"));
        categoryRepository.delete(category);
    }

    public CategoryResponse mapper(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
