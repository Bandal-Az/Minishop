package com.example.minishop.repository;

import com.example.minishop.entity.EmailVerification;
import com.example.minishop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByMember(Member member);
    Optional<EmailVerification> findByVerificationCode(String code);
}
