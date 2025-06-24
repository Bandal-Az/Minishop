package com.example.minishop.service;

import com.example.minishop.dto.orderitem.OrderItemRequestDto;
import com.example.minishop.dto.orderitem.OrderItemResponseDto;
import com.example.minishop.entity.Order;
import com.example.minishop.entity.OrderItem;
import com.example.minishop.entity.Product;
import com.example.minishop.repository.OrderItemRepository;
import com.example.minishop.repository.OrderRepository;
import com.example.minishop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderItemResponseDto> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public OrderItemResponseDto getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public OrderItemResponseDto createOrderItem(OrderItemRequestDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 서버에서 상품 가격을 가져와서 설정
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .price(product.getPrice())  // dto.getPrice() 대신 product 가격 사용
                .build();

        OrderItem saved = orderItemRepository.save(item);
        return toResponseDto(saved);
    }

    public OrderItemResponseDto updateOrderItem(Long id, OrderItemRequestDto dto) {
        return orderItemRepository.findById(id)
                .map(existing -> {
                    existing.setQuantity(dto.getQuantity());
                    // 가격도 서버에서 다시 가져오거나, 그대로 유지할 수도 있음
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    existing.setPrice(product.getPrice());
                    return toResponseDto(orderItemRepository.save(existing));
                })
                .orElse(null);
    }

    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }

    private OrderItemResponseDto toResponseDto(OrderItem item) {
        return OrderItemResponseDto.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productId(item.getProduct().getId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice()) // 총 가격 계산 필드 추가
                .build();
    }
}
