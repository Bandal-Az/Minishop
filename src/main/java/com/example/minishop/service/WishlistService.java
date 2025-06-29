package com.example.minishop.service;

import com.example.minishop.dto.wishlist.WishlistRequestDto;
import com.example.minishop.dto.wishlist.WishlistResponseDto;
import com.example.minishop.entity.Member;
import com.example.minishop.entity.Product;
import com.example.minishop.entity.Wishlist;
import com.example.minishop.repository.MemberRepository;
import com.example.minishop.repository.ProductRepository;
import com.example.minishop.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 로그인한 사용자의 위시리스트 목록 조회
    public List<WishlistResponseDto> getAllWishlists(Long memberId) {
        return wishlistRepository.findByMemberId(memberId).stream() // 로그인한 사용자의 위시리스트만 조회
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 특정 ID로 위시리스트 조회
    public WishlistResponseDto getWishlistById(Long id, Long memberId) {
        return wishlistRepository.findByIdAndMemberId(id, memberId)
                .map(this::toResponseDto)
                .orElse(null);
    }

    // 위시리스트 항목 생성
    public WishlistResponseDto createWishlist(WishlistRequestDto dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = Wishlist.builder()
                .member(member)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);
        return toResponseDto(saved);
    }

    // 위시리스트 항목 수정
    public WishlistResponseDto updateWishlist(Long id, WishlistRequestDto dto, Long memberId) {
        return wishlistRepository.findByIdAndMemberId(id, memberId)
                .map(existing -> {
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    existing.setProduct(product);
                    return toResponseDto(wishlistRepository.save(existing));
                })
                .orElse(null);
    }

    // 위시리스트 항목 삭제
    public void deleteWishlist(Long id, Long memberId) {
        wishlistRepository.findByIdAndMemberId(id, memberId)
                .ifPresent(wishlistRepository::delete);
    }

    // Wishlist -> WishlistResponseDto 변환
    private WishlistResponseDto toResponseDto(Wishlist wishlist) {
        return WishlistResponseDto.from(wishlist);  // 위에서 작성한 from 메서드 사용
    }
}
