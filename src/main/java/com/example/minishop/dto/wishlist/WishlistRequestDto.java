package com.example.minishop.dto.wishlist;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistRequestDto {
    private Long memberId;
    private Long productId;
}
