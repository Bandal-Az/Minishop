package com.example.minishop.dto.product;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {
    private Long id;           // DB 저장 후 아이디
    private String imageUrl;
    private Boolean isThumbnail;
}

