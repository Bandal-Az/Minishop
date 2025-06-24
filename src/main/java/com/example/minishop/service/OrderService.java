package com.example.minishop.service;

import com.example.minishop.dto.orderitem.OrderItemRequestDto;
import com.example.minishop.dto.order.OrderRequestDto;
import com.example.minishop.dto.order.OrderResponseDto;
import com.example.minishop.dto.orderitem.OrderItemResponseDto;
import com.example.minishop.entity.Member;
import com.example.minishop.entity.Order;
import com.example.minishop.entity.Order.OrderStatus;
import com.example.minishop.entity.OrderItem;
import com.example.minishop.entity.Product;
import com.example.minishop.repository.MemberRepository;
import com.example.minishop.repository.OrderRepository;
import com.example.minishop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public OrderResponseDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public OrderResponseDto createOrder(OrderRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Order order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())   // 서버에서 현재 시간 지정
                .status(OrderStatus.ORDERED)      // 기본 상태 지정
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : dto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.addOrderItem(orderItem);

            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        order.setTotalPrice(totalPrice);

        Order saved = orderRepository.save(order);
        return toResponseDto(saved);
    }

    public OrderResponseDto updateOrder(Long id, OrderRequestDto dto) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.getOrderItems().clear();  // 기존 주문 상품 모두 삭제

                    BigDecimal totalPrice = BigDecimal.ZERO;

                    for (OrderItemRequestDto itemDto : dto.getOrderItems()) {
                        Product product = productRepository.findById(itemDto.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                        OrderItem orderItem = OrderItem.builder()
                                .order(existing)
                                .product(product)
                                .quantity(itemDto.getQuantity())
                                .price(product.getPrice())
                                .build();

                        existing.addOrderItem(orderItem);

                        totalPrice = totalPrice.add(orderItem.getTotalPrice());
                    }

                    existing.setTotalPrice(totalPrice);

                    // 상태나 날짜는 여기서 필요하면 업데이트, 예시로 그대로 두겠습니다.

                    return toResponseDto(orderRepository.save(existing));
                })
                .orElse(null);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderResponseDto toResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(order.getId())
                .memberId(order.getMember().getId())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .orderItems(itemDtos)
                .build();
    }
}
