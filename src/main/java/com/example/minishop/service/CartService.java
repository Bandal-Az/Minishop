package com.example.minishop.service;

import com.example.minishop.dto.cart.CartResponseDto;
import com.example.minishop.dto.cartItem.CartItemResponseDto;
import com.example.minishop.entity.Cart;
import com.example.minishop.entity.CartItem;
import com.example.minishop.entity.Member;
import com.example.minishop.entity.Product;
import com.example.minishop.repository.CartItemRepository;
import com.example.minishop.repository.CartRepository;
import com.example.minishop.repository.MemberRepository;
import com.example.minishop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CartResponseDto getCartByMemberId(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createCartForMember(memberId));
        return toResponseDto(cart);
    }

    @Transactional
    public Cart createCartForMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Cart cart = Cart.builder()
                .member(member)
                .build();
        return cartRepository.save(cart);
    }

    @Transactional
    public CartResponseDto addOrUpdateCartItem(Long memberId, Long productId, int quantity) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createCartForMember(memberId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> cartItems = cart.getCartItems();

        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
        }

        return toResponseDto(cart);
    }

    @Transactional
    public CartResponseDto updateCartItem(Long memberId, Long cartItemId, int quantity) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null) {
            cartItems = Collections.emptyList();
        }

        CartItem item = cartItems.stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        if (quantity <= 0) {
            cart.removeCartItem(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return toResponseDto(cart);
    }

    @Transactional
    public CartResponseDto removeCartItem(Long memberId, Long cartItemId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null) {
            cartItems = Collections.emptyList();
        }

        CartItem item = cartItems.stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        cart.removeCartItem(item);
        cartItemRepository.delete(item);

        return toResponseDto(cart);
    }

    private CartResponseDto toResponseDto(Cart cart) {
        List<CartItemResponseDto> cartItems = cart.getCartItems() == null ? Collections.emptyList() :
                cart.getCartItems().stream()
                        .map(this::toCartItemResponseDto)
                        .toList();

        return CartResponseDto.builder()
                .cartId(cart.getId())
                .memberId(cart.getMember().getId())
                .totalPrice(cart.getTotalPrice())
                .cartItems(cartItems)
                .build();
    }

    private CartItemResponseDto toCartItemResponseDto(CartItem item) {
        String imageUrl = null;
        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            imageUrl = item.getProduct().getImages().get(0).getImageUrl();
        }
        return CartItemResponseDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice())
                .productImageUrl(imageUrl)  // 여기만 바꿈
                .build();
    }
}
