package com.example.minishop.dto.cartItem;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDto {
    private Long id;           // CartItem ID
    private Long productId;    // 상품 ID
    private String productName; // 상품명 (Entity에서 Product 이름 매핑 시 필요)
    private int quantity;      // 수량
    private BigDecimal price;  // 단가
    private BigDecimal totalPrice; // 총 가격 (단가 * 수량)
    private String productImageUrl; // 이미지
}
