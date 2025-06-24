package com.example.minishop.dto.wishlist;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponseDto {
    private Long id;
    private Long memberId;
    private Long productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
