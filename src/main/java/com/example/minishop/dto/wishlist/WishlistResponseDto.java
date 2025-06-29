package com.example.minishop.dto.wishlist;

import com.example.minishop.entity.Product;
import com.example.minishop.entity.ProductImage;
import com.example.minishop.entity.Wishlist;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponseDto {
    private Long id;
    private Long memberId;
    private Long productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String productName;
    private BigDecimal productPrice;
    private String productDescription;
    private String productImageUrl;

    public static WishlistResponseDto from(Wishlist wishlist) {
        Product product = wishlist.getProduct();

        // 썸네일 이미지 가져오기
        String thumbnailUrl = product.getImages().stream()
                .filter(ProductImage::getIsThumbnail)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse("");  // 썸네일이 없으면 빈 문자열

        return WishlistResponseDto.builder()
                .id(wishlist.getId())
                .memberId(wishlist.getMember().getId())
                .productId(wishlist.getProduct().getId())
                .productName(wishlist.getProduct().getName())
                .productPrice(product.getPrice())
                .productDescription(wishlist.getProduct().getDescription())
                .productImageUrl(thumbnailUrl)
                .createdAt(wishlist.getCreatedAt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }
}
