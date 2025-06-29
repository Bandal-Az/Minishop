package com.example.minishop.repository;

import com.example.minishop.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // memberId로 위시리스트를 조회 (여러 개의 위시리스트가 있을 수 있음)
    @Query("SELECT w FROM Wishlist w WHERE w.member.id = :memberId")
    List<Wishlist> findByMemberId(@Param("memberId") Long memberId);

    // 특정 ID와 memberId로 위시리스트 조회 (하나의 위시리스트)
    @Query("SELECT w FROM Wishlist w WHERE w.id = :id AND w.member.id = :memberId")
    Optional<Wishlist> findByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
}
