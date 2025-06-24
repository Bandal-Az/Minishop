package com.example.minishop.dto.category;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;

    private Long parentId;    // 부모 카테고리 ID
    private String parentName; // 부모 카테고리 이름

    private List<SimpleCategoryDto> children;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleCategoryDto {
        private Long id;
        private String name;
    }
}
