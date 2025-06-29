package com.example.minishop.service;

import com.example.minishop.entity.EmailVerification;
import com.example.minishop.entity.Member;
import com.example.minishop.repository.EmailVerificationRepository;
import com.example.minishop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final EmailSenderService emailSenderService;

    @Value("${app.email-verification.expiry-minutes:10}")
    private int expiryMinutes;

    public boolean sendVerificationEmailByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(member -> {
                    sendVerificationEmail(member.getEmail());
                    return true;
                })
                .orElse(false);
    }

    /**
     * 이메일 인증 코드 생성 및 이메일 발송
     */
    public void sendVerificationEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 이메일이 아닙니다."));

        // 기존 인증 정보가 있으면 삭제 (새로 발급)
        emailVerificationRepository.findByMember(member)
                .ifPresent(emailVerificationRepository::delete);

        // 영문 + 숫자 혼합 인증 코드 생성 (8자리)
        String code = generateRandomCode(8);

        EmailVerification emailVerification = EmailVerification.builder()
                .member(member)
                .verificationCode(code)
                .expiryDate(LocalDateTime.now().plusMinutes(expiryMinutes))
                .verified(false)
                .build();

        emailVerificationRepository.save(emailVerification);

        // 이메일 내용 구성
        String subject = "Minishop 이메일 인증 코드";
        String text = String.format(
                "안녕하세요! Minishop 이메일 인증 코드입니다.\n\n인증 코드는 [%s] 입니다.\n유효기간은 %d분입니다.\n\n감사합니다.",
                code, expiryMinutes
        );

        // 이메일 전송
        emailSenderService.sendEmail(email, subject, text);
    }

    /**
     * 인증 코드 검증
     */
    public boolean verifyCode(String email, String code) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member == null) {
            return false;
        }

        EmailVerification emailVerification = emailVerificationRepository.findByMember(member).orElse(null);
        if (emailVerification == null || emailVerification.isExpired() || emailVerification.getVerified()) {
            return false;
        }

        if (!emailVerification.getVerificationCode().equals(code)) {
            return false;
        }

        // 인증 완료 처리
        emailVerification.setVerified(true);
        emailVerificationRepository.save(emailVerification);

        // Member 엔티티 이메일 인증 상태 업데이트
        member.setIsEmailVerified(true);
        memberRepository.save(member);

        return true;
    }

    /**
     * 영문 + 숫자 조합 랜덤 인증 코드 생성
     */
    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 대문자 + 숫자
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
