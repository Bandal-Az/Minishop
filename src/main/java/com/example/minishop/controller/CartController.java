package com.example.minishop.controller;

import com.example.minishop.dto.cart.CartRequestDto;
import com.example.minishop.dto.cart.CartResponseDto;
import com.example.minishop.security.service.MemberDetails;
import com.example.minishop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    //    회원의 장바구니 조회
    @PreAuthorize("isAuthenticated()") // 로그인 사용자만 불러와짐
    @GetMapping
    public CartResponseDto getCart(@AuthenticationPrincipal MemberDetails userDetails) {
        return cartService.getCartByMemberId(userDetails.getId());
    }

    //    장바구니에 상품 추가 또는 수량 증가
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/items")
    public CartResponseDto addItemToCart(
            @AuthenticationPrincipal MemberDetails userDetails,
            @RequestBody CartRequestDto requestDto
    ) {
        return cartService.addOrUpdateCartItem(userDetails.getId(), requestDto.getProductId(), requestDto.getQuantity());
    }

    //    장바구니 상품 수량 수정
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/items/{cartItemId}")
    public CartResponseDto updateCartItem(
            @AuthenticationPrincipal MemberDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        return cartService.updateCartItem(userDetails.getId(), cartItemId, quantity);
    }

    //    장바구니에서 상품 제거
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/items/{cartItemId}")
    public CartResponseDto removeCartItem(
            @AuthenticationPrincipal MemberDetails userDetails,
            @PathVariable Long cartItemId
    ) {
        return cartService.removeCartItem(userDetails.getId(), cartItemId);
    }

}
