package com.example.minishop.controller;

import com.example.minishop.dto.orderitem.OrderItemRequestDto;
import com.example.minishop.dto.orderitem.OrderItemResponseDto;
import com.example.minishop.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;


//  전체 주문 상품 목록 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderItemResponseDto>> getAllOrderItems() {
        List<OrderItemResponseDto> items = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(items);
    }


//  ID로 주문 상품 단건 조회
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Long id) {
        OrderItemResponseDto item = orderItemService.getOrderItemById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }


//  주문 상품 생성
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderItemResponseDto> createOrderItem(@RequestBody OrderItemRequestDto dto) {
        OrderItemResponseDto createdItem = orderItemService.createOrderItem(dto);
        return ResponseEntity.ok(createdItem);
    }

//  주문 상품 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderItemResponseDto> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemRequestDto dto) {
        OrderItemResponseDto updatedItem = orderItemService.updateOrderItem(id, dto);
        if (updatedItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedItem);
    }


//    주문 상품 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
