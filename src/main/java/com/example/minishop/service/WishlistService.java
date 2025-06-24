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

    public List<WishlistResponseDto> getAllWishlists() {
        return wishlistRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public WishlistResponseDto getWishlistById(Long id) {
        return wishlistRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public WishlistResponseDto createWishlist(WishlistRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
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

    public WishlistResponseDto updateWishlist(Long id, WishlistRequestDto dto) {
        return wishlistRepository.findById(id)
                .map(existing -> {
                    Member member = memberRepository.findById(dto.getMemberId())
                            .orElseThrow(() -> new RuntimeException("Member not found"));
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    existing.setMember(member);
                    existing.setProduct(product);
                    return toResponseDto(wishlistRepository.save(existing));
                })
                .orElse(null);
    }

    public void deleteWishlist(Long id) {
        wishlistRepository.deleteById(id);
    }

    private WishlistResponseDto toResponseDto(Wishlist wishlist) {
        return WishlistResponseDto.builder()
                .id(wishlist.getId())
                .memberId(wishlist.getMember().getId())
                .productId(wishlist.getProduct().getId())
                .build();
    }
}
