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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // 모든 주문 항목 조회
    public List<OrderItemResponseDto> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 주문 항목 ID로 조회
    public OrderItemResponseDto getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    // 주문 항목 생성
    public OrderItemResponseDto createOrderItem(OrderItemRequestDto dto) {
        // 주문 및 상품 조회
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 가격 확인: null일 경우 예외 처리
        BigDecimal price = product.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Product price is invalid or not set.");
        }

        // 주문 항목 생성
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .price(product.getPrice())  // 정확한 가격 설정
                .build();

        // 주문 항목 저장
        OrderItem saved = orderItemRepository.save(item);

        // 저장된 항목을 응답 DTO로 변환
        return toResponseDto(saved);
    }

    // 주문 항목 업데이트
    public OrderItemResponseDto updateOrderItem(Long id, OrderItemRequestDto dto) {
        return orderItemRepository.findById(id)
                .map(existing -> {
                    existing.setQuantity(dto.getQuantity());

                    // 상품 가격 재확인
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    // 가격이 null이거나 잘못된 경우 예외 처리
                    BigDecimal price = product.getPrice();
                    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RuntimeException("Product price is invalid or not set.");
                    }

                    // 가격 업데이트
                    existing.setPrice(price);

                    // 업데이트된 주문 항목 저장
                    return toResponseDto(orderItemRepository.save(existing));
                })
                .orElse(null);
    }

    // 주문 항목 삭제
    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }

    // OrderItem을 OrderItemResponseDto로 변환
    private OrderItemResponseDto toResponseDto(OrderItem item) {
        // 상품 이미지 URL 처리
        String imageUrl = null;
        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            imageUrl = item.getProduct().getImages().get(0).getImageUrl();
        }

        // DTO로 반환
        return OrderItemResponseDto.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productId(item.getProduct().getId())
                .quantity(item.getQuantity())
                .price(item.getPrice())  // 가격 정보
                .totalPrice(item.getTotalPrice())  // 총 가격 계산 필드 추가
                .productName(item.getProduct().getName())  // 상품명 추가
                .productImageUrl(imageUrl)  // 상품 이미지 URL 추가
                .build();
    }
}
