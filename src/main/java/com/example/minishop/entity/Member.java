package com.example.minishop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class Member extends BaseEntity {  // BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;  // 로그인 아이디 (유니크)

    @Column(unique = true, length = 50)
    private String nickname;  // 프로필 닉네임 (유니크, 중복 불가)

    @Column(nullable = false, unique = true, length = 100)
    private String email;     // 이메일 (유니크)

    @Column(nullable = false)
    private String password;  // 암호화된 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;        // 회원 역할

    @Column // 실명
    private String realName;

    @Column(nullable = false, unique = true, length = 20) // 전화번호
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Column(nullable = false)
    private Boolean isEmailVerified = false; // 이메일 인증 여부

    // 간편인증 관련 필드
    @Column(nullable = false)
    private Boolean isPhoneAuthVerified = false;  // 간편인증 성공 여부

    @Column(length = 255)
    private String phoneAuthToken;  // 간편인증 토큰

    @Column(length = 50)
    private String authProvider;    // 인증 제공자 (예: PASS, TOSS)

    private Boolean isActive; // BaseEntity에 없다면 여기에 유지

    public enum Role {
        CLIENT, ADMIN
    }
}
