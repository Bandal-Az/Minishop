package com.example.minishop.service;

import com.example.minishop.dto.review.ReviewRequestDto;
import com.example.minishop.dto.review.ReviewResponseDto;
import com.example.minishop.entity.Member;
import com.example.minishop.entity.Product;
import com.example.minishop.entity.Review;
import com.example.minishop.repository.MemberRepository;
import com.example.minishop.repository.ProductRepository;
import com.example.minishop.repository.ReviewRepository;
import com.example.minishop.util.FileService; // 파일 업로드 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final FileService fileService;

    // 전체 리뷰 조회
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 특정 리뷰 조회
    public ReviewResponseDto getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    // 리뷰 생성
    public ReviewResponseDto createReview(ReviewRequestDto dto, MultipartFile file, Long memberId) {
        // 로그인된 사용자의 정보 이용하여 memberId 설정
        dto.setMemberId(memberId);

        Review review = toEntity(dto, file);
        Review saved = reviewRepository.save(review);

        return toResponseDto(saved);
    }

    // 리뷰 수정
    public ReviewResponseDto updateReview(Long id, ReviewRequestDto dto, MultipartFile file) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setRating(dto.getRating());
                    existing.setComment(dto.getComment());

                    // 파일이 존재하면 이미지 URL 업데이트
                    if (file != null) {
                        String imageUrl = fileService.saveFile(file); // 파일 저장 후 URL 받기
                        existing.setImageUrl(imageUrl);
                    }

                    return toResponseDto(reviewRepository.save(existing));
                })
                .orElse(null);
    }

    // 리뷰 삭제
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    // ReviewResponseDto 변환
    private ReviewResponseDto toResponseDto(Review review) {
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

    // Review 엔티티 변환
    private Review toEntity(ReviewRequestDto dto, MultipartFile file) {
        // Check if memberId and productId are present
        if (dto.getMemberId() == null || dto.getProductId() == null) {
            throw new IllegalArgumentException("Member ID or Product ID cannot be null");
        }

        // Logging for debug purposes
        System.out.println("Member ID: " + dto.getMemberId());
        System.out.println("Product ID: " + dto.getProductId());

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Logging to check if we retrieved member and product successfully
        System.out.println("Retrieved Member: " + member);
        System.out.println("Retrieved Product: " + product);

        String imageUrl = null;
        if (file != null) {
            imageUrl = fileService.saveFile(file); // 파일 저장 후 URL 받기
        }

        return Review.builder()
                .member(member)
                .product(product)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .imageUrl(imageUrl) // 이미지 URL 포함
                .build();
    }
}