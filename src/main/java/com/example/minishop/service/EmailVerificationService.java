package com.example.minishop.service;

import com.example.minishop.entity.EmailVerification;
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
    private final MemberRepository memberRepository;  // 회원 가입 시 이메일 인증 상태 업데이트에 사용
    private final EmailSenderService emailSenderService;

    @Value("${app.email-verification.expiry-minutes:10}")
    private int expiryMinutes;

    /**
     * 이메일 인증 코드 발송 (회원 존재 여부 관계 없이 무조건 발송)
     */
    public boolean sendVerificationEmailByEmail(String email) {
        sendVerificationEmail(email);
        return true;
    }

    public void sendVerificationEmail(String email) {
        // 기존 인증 정보가 있으면 삭제 (새로 발급)
        emailVerificationRepository.findByEmail(email)
                .ifPresent(emailVerificationRepository::delete);

        String code = generateRandomCode(8);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .verificationCode(code)
                .expiryDate(LocalDateTime.now().plusMinutes(expiryMinutes))
                .verified(false)
                .build();

        emailVerificationRepository.save(emailVerification);

        String subject = "Minishop 이메일 인증 코드";
        String text = String.format(
                "안녕하세요! Minishop 이메일 인증 코드입니다.\n\n인증 코드는 [%s] 입니다.\n유효기간은 %d분입니다.\n\n감사합니다.",
                code, expiryMinutes
        );

        emailSenderService.sendEmail(email, subject, text);
    }

    /**
     * 인증 코드 검증
     */
    public boolean verifyCode(String email, String code) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email).orElse(null);
        if (emailVerification == null || emailVerification.isExpired() || emailVerification.getVerified()) {
            return false;
        }

        if (!emailVerification.getVerificationCode().equals(code)) {
            return false;
        }

        // 인증 완료 처리
        emailVerification.setVerified(true);
        emailVerificationRepository.save(emailVerification);

        // 회원 가입 후 이메일 인증 상태 업데이트를 위해 회원이 존재하면 상태 변경
        memberRepository.findByEmail(email).ifPresent(member -> {
            member.setIsEmailVerified(true);
            memberRepository.save(member);
        });

        return true;
    }

    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
