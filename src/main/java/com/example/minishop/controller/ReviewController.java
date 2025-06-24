package com.example.minishop.controller;

import com.example.minishop.dto.review.ReviewRequestDto;
import com.example.minishop.dto.review.ReviewResponseDto;
import com.example.minishop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return reviewService.getReviewById(id);
    }

    // 새 리뷰 생성
    @PostMapping
    public ReviewResponseDto createReview(@RequestBody ReviewRequestDto dto) {
        return reviewService.createReview(dto);
    }

    // 기존 리뷰 수정
    @PutMapping("/{id}")
    public ReviewResponseDto updateReview(@PathVariable Long id, @RequestBody ReviewRequestDto dto) {
        return reviewService.updateReview(id, dto);
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
