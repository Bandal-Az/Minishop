package com.example.minishop.controller;

import com.example.minishop.dto.category.CategoryRequestDto;
import com.example.minishop.dto.category.CategoryResponseDto;
import com.example.minishop.dto.product.ProductResponseDto;
import com.example.minishop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 목록 조회
    @GetMapping
    public List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // 특정 카테고리 조회
    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // 새 카테고리 생성
    @PostMapping
    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto dto) {
        return categoryService.createCategory(dto);
    }

    // 기존 카테고리 수정
    @PutMapping("/{id}")
    public CategoryResponseDto updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDto dto) {
        return categoryService.updateCategory(id, dto);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

}
