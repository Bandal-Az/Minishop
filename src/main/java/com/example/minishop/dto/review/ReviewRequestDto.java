package com.example.minishop.dto.review;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile imageFile;
}
