package com.example.minishop.service;

import com.example.minishop.dto.member.MemberRequestDto;
import com.example.minishop.dto.member.MemberResponseDto;
import com.example.minishop.entity.Member;
import com.example.minishop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.minishop.entity.Member.Role.CLIENT;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 이메일 인증은 별도 EmailVerificationService에서 처리 (필요시 주입 가능)
    // private final EmailVerificationService emailVerificationService;

    public List<MemberResponseDto> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public MemberResponseDto getMemberById(Long id) {
        return memberRepository.findById(id)
                .map(this::toResponseDto)
                .orElse(null);
    }

    public MemberResponseDto createMember(MemberRequestDto dto) {
        if (isUsernameDuplicate(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (isEmailDuplicate(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (!isValidPassword(dto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 정책에 맞지 않습니다.");
        }

        Member member = toEntity(dto);
        member.setPassword(passwordEncoder.encode(dto.getPassword())); // 암호화 적용

        Member saved = memberRepository.save(member);

        // 이메일 인증 시작 로직 호출 가능
        // emailVerificationService.sendVerificationEmail(saved);

        return toResponseDto(saved);
    }

    public MemberResponseDto updateMember(Long id, MemberRequestDto dto) {
        return memberRepository.findById(id)
                .map(existing -> {
                    // 중복 검사
                    if (!existing.getUsername().equals(dto.getUsername()) && isUsernameDuplicate(dto.getUsername())) {
                        throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
                    }
                    if (!existing.getEmail().equals(dto.getEmail()) && isEmailDuplicate(dto.getEmail())) {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                    }

                    existing.setUsername(dto.getUsername());
                    existing.setEmail(dto.getEmail());
                    existing.setNickname(dto.getNickname());
                    existing.setRealName(dto.getRealName());
                    existing.setPhoneNumber(dto.getPhoneNumber());
                    existing.setAddress(dto.getAddress());

                    // 이메일 인증 여부는 이메일 인증 서비스에서 관리, 직접 변경하지 않음
                    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                        }
                        if (!isValidPassword(dto.getPassword())) {
                            throw new IllegalArgumentException("비밀번호가 정책에 맞지 않습니다.");
                        }
                        existing.setPassword(passwordEncoder.encode(dto.getPassword()));
                    }

                    return toResponseDto(memberRepository.save(existing));
                })
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean isUsernameDuplicate(String username) {
        return memberRepository.existsByUsername(username);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;

        // 영문자, 숫자, 특수문자 포함 여부 체크
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
        if (!password.matches(pattern)) return false;

        // 같은 문자 3회 이상 연속 사용 금지
        return !password.matches(".*(.)\\1\\1.*");
    }

    private MemberResponseDto toResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .realName(member.getRealName())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .isEmailVerified(member.getIsEmailVerified()) // 이메일 인증 상태 포함
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    private Member toEntity(MemberRequestDto dto) {
        return Member.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())  // 암호화는 서비스에서 처리
                .nickname(dto.getNickname())
                .realName(dto.getRealName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .isEmailVerified(false) // 기본 false로 초기화
                .isActive(true)
                .role(CLIENT)
                .build();
    }

    public boolean login(String username, String rawPassword) {
        Member member = memberRepository.findByUsername(username).orElse(null);
        if (member == null) {
            System.out.println("로그인 실패: 사용자 없음 -> " + username);
            return false;
        }
        boolean matches = passwordEncoder.matches(rawPassword, member.getPassword());
        System.out.println("로그인 시도: " + username + ", 비밀번호 일치 여부: " + matches);
        return matches;
    }

}
