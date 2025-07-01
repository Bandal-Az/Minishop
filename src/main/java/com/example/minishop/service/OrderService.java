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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // 주문 객체 생성
        Order order = Order.builder()
                .member(member)
                .isActive(true)
                .orderDate(LocalDateTime.now())   // 서버에서 현재 시간 지정
                .status(OrderStatus.ORDERED)      // 기본 상태 지정
                .build();

        // 총 가격 계산 초기화
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 주문 항목 추가
        for (OrderItemRequestDto itemDto : dto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // 가격을 BigDecimal로 계산 후 소수점 2자리 반올림 처리
            BigDecimal itemTotalPrice = calculateItemTotalPrice(product, itemDto.getQuantity());

            // 주문 항목 객체 생성
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .price(product.getPrice())  // 가격은 실제 가격
                    .build();

            order.addOrderItem(orderItem);  // 주문에 항목 추가

            // 총 가격 업데이트
            totalPrice = totalPrice.add(itemTotalPrice);  // 각 항목의 가격을 합산
        }

        // 주문의 총 가격 설정
        order.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));  // 총 가격 소수점 2자리 반올림

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 응답 DTO 반환
        return toResponseDto(savedOrder);
    }

    public OrderResponseDto updateOrder(Long id, OrderRequestDto dto) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    existingOrder.getOrderItems().clear();  // 기존 주문 상품 모두 삭제

                    BigDecimal totalPrice = BigDecimal.ZERO;

                    // 새 주문 항목 추가
                    for (OrderItemRequestDto itemDto : dto.getOrderItems()) {
                        Product product = productRepository.findById(itemDto.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                        // 가격 계산 및 반올림 처리
                        BigDecimal itemTotalPrice = calculateItemTotalPrice(product, itemDto.getQuantity());

                        // 주문 항목 생성
                        OrderItem orderItem = OrderItem.builder()
                                .order(existingOrder)
                                .product(product)
                                .quantity(itemDto.getQuantity())
                                .price(product.getPrice())  // 가격 설정
                                .build();

                        existingOrder.addOrderItem(orderItem);

                        // 총 가격 갱신
                        totalPrice = totalPrice.add(itemTotalPrice);
                    }

                    // 주문의 총 가격 업데이트
                    existingOrder.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));  // 소수점 2자리 반올림

                    // 업데이트된 주문 저장
                    return toResponseDto(orderRepository.save(existingOrder));
                })
                .orElse(null);
    }

    @Transactional // 트랜잭션 적용
    public OrderResponseDto cancelOrder(Long orderId) {
        // 1. 주문 ID로 주문을 찾습니다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        // 2. 주문 상태 확인 (이미 배송 중이거나 완료된 주문은 취소할 수 없습니다.)
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order cannot be cancelled in its current status: " + order.getStatus());
        }

        // 3. 주문 상태를 CANCELLED로 변경합니다.
        order.setStatus(OrderStatus.CANCELLED);

        // 4. 변경된 주문을 저장합니다.
        Order cancelledOrder = orderRepository.save(order);

        // 5. 응답 DTO로 변환하여 반환합니다.
        return toResponseDto(cancelledOrder);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }


    // 주문 항목의 총 가격을 계산하는 로직을 별도 메서드로 분리
    private BigDecimal calculateItemTotalPrice(Product product, int quantity) {
        BigDecimal price = product.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Product price is invalid or not set.");
        }
        return price.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);  // 가격 * 수량
    }

    // Order 객체를 DTO로 변환
    private OrderResponseDto toResponseDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.getTotalPrice())  // 총 가격 포함
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(order.getId())
                .memberId(order.getMember().getId())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())  // 총 가격 포함
                .status(order.getStatus().name())
                .orderItems(itemDtos)
                .build();
    }
}
