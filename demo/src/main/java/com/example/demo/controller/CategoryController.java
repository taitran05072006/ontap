package com.example.demo.controller;

import com.example.demo.dto.category.CategoryRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{category_id}")
    public CategoryResponse getById(@PathVariable long category_id) {
        return categoryService.getById(category_id);
    }

    @GetMapping("/search")
    public CategoryResponse getByName(@RequestParam String name) {
        return categoryService.findByName(name);
    }

    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }
    @PutMapping("/{category_id}")
    public CategoryResponse update(@PathVariable long category_id, @RequestBody CategoryRequest request) {
        return categoryService.update(category_id, request);
    }

    @DeleteMapping("/{category_id}")
    public void delete(@PathVariable long category_id) {
        categoryService.delete(category_id);
    }
}
