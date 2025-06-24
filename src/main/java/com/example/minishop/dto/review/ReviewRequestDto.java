package com.example.minishop.dto.review;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    private Long productId;
    private Long memberId;
    private int rating;
    private String comment;
}
