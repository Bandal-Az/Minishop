package com.example.minishop.controller;

import com.example.minishop.dto.order.OrderRequestDto;
import com.example.minishop.dto.order.OrderResponseDto;
import com.example.minishop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

//  전체 주문 목록 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


//  ID로 주문 단건 조회
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

//  주문 생성
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto dto) {
        System.out.println("주문 생성 요청: " + dto);  // 로그 추가
        OrderResponseDto createdOrder = orderService.createOrder(dto);
        return ResponseEntity.ok(createdOrder);
    }

//  주문 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody OrderRequestDto dto) {
        OrderResponseDto updatedOrder = orderService.updateOrder(id, dto);
        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/cancel") // PATCH 메서드를 사용하여 부분 업데이트 (상태 변경)
    @PreAuthorize("hasRole('USER')") // 사용자가 자신의 주문을 취소할 수 있도록 USER 권한 부여
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id) {
        OrderResponseDto cancelledOrder = orderService.cancelOrder(id); // OrderService에 cancelOrder 메서드 필요
        if (cancelledOrder == null) {
            return ResponseEntity.notFound().build(); // 주문이 없거나 취소할 수 없는 상태인 경우
        }
        return ResponseEntity.ok(cancelledOrder);
    }


//    주문 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
