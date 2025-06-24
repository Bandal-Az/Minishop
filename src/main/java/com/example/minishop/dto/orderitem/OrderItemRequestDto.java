package com.example.minishop.dto.orderitem;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDto {
    private Long orderId;
    private Long productId;
    private int quantity;
}
