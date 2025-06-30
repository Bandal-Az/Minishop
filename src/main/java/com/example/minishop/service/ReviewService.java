package com.example.minishop.service;

import com.example.minishop.dto.review.ReviewRequestDto;
import com.example.minishop.dto.review.ReviewResponseDto;
import com.example.minishop.entity.Member;
import com.example.minishop.entity.Product;
import com.example.minishop.entity.Review;
import com.example.minishop.repository.MemberRepository;
import com.example.minishop.repository.ProductRepository;
import com.example.minishop.repository.ReviewRepository;
import com.example.minishop.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        String imageUrl = null;
        MultipartFile imageFile = requestDto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileService.saveFile(imageFile);
        }

        Review review = Review.builder()
                .product(product)
                .member(member)
                .rating(requestDto.getRating())
                .comment(requestDto.getComment())
                .imageUrl(imageUrl)
                .isActive(true)
                .build();

        return toDto(reviewRepository.save(review));
    }

    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        if (!review.getMember().getId().equals(requestDto.getMemberId())) {
            throw new IllegalArgumentException("본인의 리뷰만 수정할 수 있습니다.");
        }

        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());

        MultipartFile imageFile = requestDto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileService.saveFile(imageFile);
            review.setImageUrl(imageUrl);
        }

        return toDto(reviewRepository.save(review));
    }

    public List<ReviewResponseDto> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdAndIsActiveTrue(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewResponseDto> getReviewsByMemberId(Long memberId) {
        return reviewRepository.findByMemberIdAndIsActiveTrue(memberId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ReviewResponseDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));
        return toDto(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .memberId(review.getMember().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
