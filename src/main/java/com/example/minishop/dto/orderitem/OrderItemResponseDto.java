package com.example.minishop.dto.orderitem;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice; // 계산된 총 가격
    private String productName;
    private String productImageUrl;
}
