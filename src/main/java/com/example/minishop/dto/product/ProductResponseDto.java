package com.example.minishop.dto.product;

import com.example.minishop.entity.Product;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private String categoryName; // category.name을 직접 매핑해서 전달
    private List<String> imageUrls;

}
