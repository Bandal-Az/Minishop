package com.example.minishop.controller;

import com.example.minishop.dto.member.MemberResponseDto;
import com.example.minishop.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.minishop.security.service.MemberDetails;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        System.out.println("AuthController#getCurrentUser - authentication = " + authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("로그인 필요!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        MemberDetails userDetails = (MemberDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();

        MemberResponseDto dto = MemberResponseDto.fromEntity(member);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/member/logined")
    public ResponseEntity<?> checkSession(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("로그인됨");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 안됨");
        }
    }
}

