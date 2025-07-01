package com.example.minishop.repository;

import com.example.minishop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Member> findByEmail(String email);
    boolean existsByNickname(String nickname);
}
