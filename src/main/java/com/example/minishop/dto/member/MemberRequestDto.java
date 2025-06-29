package com.example.minishop.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String email;
    private String realName;
    private String phoneNumber;
    private String address;
    private Boolean isEmailVerified;
    private Boolean isActive;

}
