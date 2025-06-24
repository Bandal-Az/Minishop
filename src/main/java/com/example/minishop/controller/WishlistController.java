package com.example.minishop.controller;

import com.example.minishop.dto.wishlist.WishlistRequestDto;
import com.example.minishop.dto.wishlist.WishlistResponseDto;
import com.example.minishop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    // 전체 위시리스트 조회
    @GetMapping
    public List<WishlistResponseDto> getAllWishlists() {
        return wishlistService.getAllWishlists();
    }

    // ID로 특정 위시리스트 조회
    @GetMapping("/{id}")
    public WishlistResponseDto getWishlistById(@PathVariable Long id) {
        return wishlistService.getWishlistById(id);
    }

    // 새 위시리스트 항목 생성
    @PostMapping
    public WishlistResponseDto createWishlist(@RequestBody WishlistRequestDto dto) {
        return wishlistService.createWishlist(dto);
    }

    // 기존 위시리스트 항목 수정
    @PutMapping("/{id}")
    public WishlistResponseDto updateWishlist(@PathVariable Long id, @RequestBody WishlistRequestDto dto) {
        return wishlistService.updateWishlist(id, dto);
    }

    // 위시리스트 항목 삭제
    @DeleteMapping("/{id}")
    public void deleteWishlist(@PathVariable Long id) {
        wishlistService.deleteWishlist(id);
    }
}
