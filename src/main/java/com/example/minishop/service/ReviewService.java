package com.example.minishop.service;

import com.example.minishop.dto.review.ReviewRequestDto;
import com.example.minishop.dto.review.ReviewResponseDto;
import com.example.minishop.entity.Member;
import com.example.minishop.entity.Product;
import com.example.minishop.entity.Review;
import com.example.minishop.repository.MemberRepository;
import com.example.minishop.repository.ProductRepository;
import com.example.minishop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ReviewResponseDto getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public ReviewResponseDto createReview(ReviewRequestDto dto) {
        Review review = toEntity(dto);
        Review saved = reviewRepository.save(review);
        return toResponseDto(saved);
    }

    public ReviewResponseDto updateReview(Long id, ReviewRequestDto dto) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setRating(dto.getRating());
                    existing.setComment(dto.getComment());
                    return toResponseDto(reviewRepository.save(existing));
                })
                .orElse(null);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponseDto toResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .memberId(review.getMember().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private Review toEntity(ReviewRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        return Review.builder()
                .member(member)
                .product(product)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
    }
}
