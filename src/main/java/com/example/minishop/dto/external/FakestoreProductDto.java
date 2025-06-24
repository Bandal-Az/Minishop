package com.example.minishop.dto.external;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FakestoreProductDto {
    private Long id;
    private String title;       // 상품명
    private String description;
    private BigDecimal price;
    private String category;
    private String image;       // 이미지 URL
}
