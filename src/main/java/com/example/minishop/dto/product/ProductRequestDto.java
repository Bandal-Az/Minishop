package com.example.minishop.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId; // 연관관계는 ID로 처리

    private List<String> imageUrls; // 이미지 URL 리스트 추가
}
