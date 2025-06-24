package com.example.minishop.dto.order;

import com.example.minishop.dto.orderitem.OrderItemRequestDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private Long memberId;
    private List<OrderItemRequestDto> orderItems;
}
