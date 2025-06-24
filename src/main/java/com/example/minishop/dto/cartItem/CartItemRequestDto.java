package com.example.minishop.dto.cartItem;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDto {
    private Long productId;  // 상품 아이디
    private int quantity;    // 수량
}
