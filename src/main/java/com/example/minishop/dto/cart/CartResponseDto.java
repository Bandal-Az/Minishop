package com.example.minishop.dto.cart;

import com.example.minishop.dto.cartItem.CartItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private Long cartId;
    private Long memberId;
    private BigDecimal totalPrice;
    private List<CartItemResponseDto> cartItems;
}
