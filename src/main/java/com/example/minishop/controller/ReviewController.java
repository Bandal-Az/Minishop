package com.example.minishop.controller;

import com.example.minishop.dto.review.ReviewRequestDto;
import com.example.minishop.dto.review.ReviewResponseDto;
import com.example.minishop.security.service.MemberDetails;
import com.example.minishop.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 후기 생성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @AuthenticationPrincipal MemberDetails userDetails,
            @RequestPart("review") ReviewRequestDto reviewRequestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        reviewRequestDto.setMemberId(userDetails.getMember().getId());
        reviewRequestDto.setImageFile(imageFile);

        ReviewResponseDto createdReview = reviewService.createReview(reviewRequestDto);
        return ResponseEntity.ok(createdReview);
    }

    // 후기 수정
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberDetails userDetails,
            @RequestPart("review") ReviewRequestDto reviewRequestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        reviewRequestDto.setMemberId(userDetails.getMember().getId());
        reviewRequestDto.setImageFile(imageFile);

        ReviewResponseDto updatedReview = reviewService.updateReview(id, reviewRequestDto);
        return ResponseEntity.ok(updatedReview);
    }

    // 특정 상품의 후기 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByProductId(
            @PathVariable Long productId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // 로그인한 회원의 후기 조회
    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(@AuthenticationPrincipal MemberDetails userDetails) {
        Long memberId = userDetails.getMember().getId();
        List<ReviewResponseDto> reviews = reviewService.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(reviews);
    }

    // 후기 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal MemberDetails userDetails) {

        ReviewResponseDto review = reviewService.getReviewById(id);

        // 본인 작성 후기만 삭제 가능
        if (!review.getMemberId().equals(userDetails.getMember().getId())) {
            return ResponseEntity.status(403).build();
        }

        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
