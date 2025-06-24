package com.example.minishop.service;

import com.example.minishop.dto.category.CategoryRequestDto;
import com.example.minishop.dto.category.CategoryResponseDto;
import com.example.minishop.entity.Category;
import com.example.minishop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category category = toEntity(dto);
        Category saved = categoryRepository.save(category);
        return toResponseDto(saved);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    if (dto.getParentId() != null) {
                        Category parent = categoryRepository.findById(dto.getParentId()).orElse(null);
                        existing.setParent(parent);
                    } else {
                        existing.setParent(null);
                    }
                    return toResponseDto(categoryRepository.save(existing));
                })
                .orElse(null);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto toResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .children(category.getChildren().stream()
                        .map(child -> CategoryResponseDto.SimpleCategoryDto.builder()
                                .id(child.getId())
                                .name(child.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private Category toEntity(CategoryRequestDto dto) {
        Category parent = null;
        if (dto.getParentId() != null) {
            parent = categoryRepository.findById(dto.getParentId()).orElse(null);
        }
        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .parent(parent)
                .build();
    }
}
