package com.example.minishop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j  // 로그 사용을 위해
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("이메일 전송 성공: {}", to);
        } catch (Exception e) {
            log.error("이메일 전송 실패: {}", to, e);
            // 필요시 예외를 다시 던질 수도 있음
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
