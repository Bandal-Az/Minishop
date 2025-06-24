package com.example.minishop.repository;

import com.example.minishop.entity.Wishlist;
import com.example.minishop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByMember(Member member);
}
