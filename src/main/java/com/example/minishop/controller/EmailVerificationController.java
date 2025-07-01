package com.example.minishop.controller;

import com.example.minishop.dto.emailverification.EmailVerificationRequestDto;
import com.example.minishop.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    // 1) 인증 이메일 발송 요청 (회원가입 직후 등)
    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationEmail(@RequestParam String email) {
        boolean sent = emailVerificationService.sendVerificationEmailByEmail(email);
        if (!sent) {
            return ResponseEntity.badRequest().body("등록된 이메일이 아닙니다.");
        }
        return ResponseEntity.ok("인증 이메일이 발송되었습니다.");
    }

    // 2) 인증 코드 확인 요청
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody EmailVerificationRequestDto dto) {
        boolean verified = emailVerificationService.verifyCode(dto.getEmail(), dto.getCode());
        if (verified) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 잘못되었거나 만료되었습니다.");
        }
    }
}
