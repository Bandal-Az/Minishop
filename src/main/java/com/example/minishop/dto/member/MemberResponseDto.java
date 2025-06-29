package com.example.minishop.dto.member;

import com.example.minishop.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String realName;
    private String phoneNumber;
    private String address;
    private Boolean isEmailVerified;
    private Boolean isActive;
    private String role;  // enum Role을 String으로 변환해서 넣기
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberResponseDto fromEntity(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .realName(member.getRealName())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .isEmailVerified(member.getIsEmailVerified())
                .isActive(member.getIsActive())
                .role(member.getRole() != null ? member.getRole().name() : null)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
