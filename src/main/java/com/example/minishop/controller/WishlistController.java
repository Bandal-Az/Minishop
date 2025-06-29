package com.example.minishop.controller;

import com.example.minishop.dto.wishlist.WishlistRequestDto;
import com.example.minishop.dto.wishlist.WishlistResponseDto;
import com.example.minishop.service.WishlistService;
import com.example.minishop.security.service.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    // 로그인된 사용자만 접근할 수 있도록 @PreAuthorize 추가
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<WishlistResponseDto> getAllWishlists(@AuthenticationPrincipal MemberDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        Long memberId = userDetails.getId();  // 로그인한 사용자 ID
        return wishlistService.getAllWishlists(memberId);  // 로그인한 사용자의 위시리스트만 조회
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public WishlistResponseDto getWishlistById(@PathVariable Long id, @AuthenticationPrincipal MemberDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        Long memberId = userDetails.getId();  // 로그인한 사용자 ID
        return wishlistService.getWishlistById(id, memberId);  // 사용자 ID와 일치하는 위시리스트 조회
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public WishlistResponseDto createWishlist(@RequestBody WishlistRequestDto dto, @AuthenticationPrincipal MemberDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        Long memberId = userDetails.getId();  // 로그인한 사용자 ID
        return wishlistService.createWishlist(dto, memberId);  // 사용자 ID와 함께 위시리스트 항목 생성
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public WishlistResponseDto updateWishlist(@PathVariable Long id, @RequestBody WishlistRequestDto dto, @AuthenticationPrincipal MemberDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        Long memberId = userDetails.getId();  // 로그인한 사용자 ID
        return wishlistService.updateWishlist(id, dto, memberId);  // 사용자 ID와 함께 위시리스트 항목 수정
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteWishlist(@PathVariable Long id, @AuthenticationPrincipal MemberDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        Long memberId = userDetails.getId();  // 로그인한 사용자 ID
        wishlistService.deleteWishlist(id, memberId);  // 사용자 ID와 함께 위시리스트 항목 삭제
    }
}
