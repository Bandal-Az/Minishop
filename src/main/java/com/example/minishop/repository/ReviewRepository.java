package com.example.minishop.repository;

import com.example.minishop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndIsActiveTrue(Long productId);
    List<Review> findByMemberIdAndIsActiveTrue(Long memberId);
}
