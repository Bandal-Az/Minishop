package com.example.minishop.dto.category;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {

    private String name;
    private String description;
    private Long parentId;    // 부모 카테고리 ID
    private String parentName; // 부모 카테고리 이름
}
