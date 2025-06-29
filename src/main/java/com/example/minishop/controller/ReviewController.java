package com.example.minishop.controller;

import com.example.minishop.dto.review.ReviewRequestDto;
import com.example.minishop.dto.review.ReviewResponseDto;
import com.example.minishop.service.ReviewService;
import com.example.minishop.security.service.MemberDetails; // 로그인 사용자 정보 가져오기
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 전체 리뷰 조회
    @GetMapping
    public List<ReviewResponseDto> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // ID로 특정 리뷰 조회
    @GetMapping("/{id}")
    public ReviewResponseDto getReviewById(@PathVariable Long id) {
        ReviewResponseDto review = reviewService.getReviewById(id);

        if (review == null) {
            throw new RuntimeException("Review not found for id: " + id); // 예외 처리
        }

        return review;
    }

    // 새 리뷰 생성 (이미지 파일도 처리)
    @PostMapping
    public ReviewResponseDto createReview(@RequestBody ReviewRequestDto dto,  // @ModelAttribute -> @RequestBody로 변경
                                          @RequestParam(required = false) MultipartFile file,
                                          @AuthenticationPrincipal MemberDetails userDetails) {
        // 로그인된 사용자의 정보를 가져와서 memberId 설정
        return reviewService.createReview(dto, file, userDetails.getId());
    }

    // 리뷰 수정
    @PutMapping("/{id}")
    public ReviewResponseDto updateReview(@PathVariable Long id,
                                          @RequestBody ReviewRequestDto dto,  // @ModelAttribute -> @RequestBody로 변경
                                          @RequestParam(required = false) MultipartFile file,
                                          @AuthenticationPrincipal MemberDetails userDetails) {
        // 로그인된 사용자의 정보를 가져와서 memberId 설정
        return reviewService.updateReview(id, dto, file);
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id,
                             @AuthenticationPrincipal MemberDetails userDetails) {
        reviewService.deleteReview(id);
    }
}
