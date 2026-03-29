package com.malgn.domain.member.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.TokenResponse;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.entity.RefreshToken;
import com.malgn.domain.member.entity.Role;
import com.malgn.domain.member.repository.MemberRepository;
import com.malgn.domain.member.repository.RefreshTokenRepository;
import com.malgn.configure.security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("user@malgn.com")
                .username("사용자")
                .nickname("user")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Nested
    @DisplayName("회원가입")
    class Signup {

        private MemberRequest.Signup request;

        @BeforeEach
        void setUp() {
            request = new MemberRequest.Signup();
            ReflectionTestUtils.setField(request, "email", "new@malgn.com");
            ReflectionTestUtils.setField(request, "username", "새사용자");
            ReflectionTestUtils.setField(request, "nickname", "newuser");
            ReflectionTestUtils.setField(request, "password", "password123");
            ReflectionTestUtils.setField(request, "passwordConfirm", "password123");
        }

        @Test
        @DisplayName("성공")
        void success() {
            given(memberRepository.existsByEmail("new@malgn.com")).willReturn(false);
            given(memberRepository.existsByNickname("newuser")).willReturn(false);

            authService.signup(request);

            then(memberRepository).should(times(1)).save(any(Member.class));
        }

        @Test
        @DisplayName("실패 - 이메일 중복")
        void duplicateEmail() {
            given(memberRepository.existsByEmail("new@malgn.com")).willReturn(true);

            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT));
        }

        @Test
        @DisplayName("실패 - 닉네임 중복")
        void duplicateNickname() {
            given(memberRepository.existsByEmail("new@malgn.com")).willReturn(false);
            given(memberRepository.existsByNickname("newuser")).willReturn(true);

            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT));
        }

    }

    @Nested
    @DisplayName("로그인")
    class Login {

        private MemberRequest.Login request;

        @BeforeEach
        void setUp() {
            request = new MemberRequest.Login();
            ReflectionTestUtils.setField(request, "email", "user@malgn.com");
            ReflectionTestUtils.setField(request, "password", "password123");
        }

        @Test
        @DisplayName("성공")
        void success() {
            given(memberRepository.findByEmail("user@malgn.com")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
            given(jwtTokenProvider.createAccessToken(1L)).willReturn("accessToken");
            given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refreshToken");
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.empty());

            TokenResponse.Token result = authService.login(request);

            assertThat(result.getAccessToken()).isEqualTo("accessToken");
            assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 이메일")
        void emailNotFound() {
            given(memberRepository.findByEmail("user@malgn.com")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void wrongPassword() {
            given(memberRepository.findByEmail("user@malgn.com")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
        }

    }

    @Nested
    @DisplayName("토큰 재발급")
    class Refresh {

        private RefreshToken savedToken;

        @BeforeEach
        void setUp() {
            savedToken = RefreshToken.builder()
                    .token("validRefreshToken")
                    .member(member)
                    .build();
        }

        @Test
        @DisplayName("성공")
        void success() {
            given(jwtTokenProvider.getMemberId("validRefreshToken")).willReturn(1L);
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of(savedToken));
            given(jwtTokenProvider.createAccessToken(1L)).willReturn("newAccessToken");

            String result = authService.refresh("validRefreshToken");

            assertThat(result).isEqualTo("newAccessToken");
        }

        @Test
        @DisplayName("실패 - DB에 토큰 없음")
        void tokenNotFound() {
            given(jwtTokenProvider.getMemberId("validRefreshToken")).willReturn(1L);
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refresh("validRefreshToken"))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
        }

        @Test
        @DisplayName("실패 - 토큰 불일치")
        void tokenMismatch() {
            given(jwtTokenProvider.getMemberId("differentToken")).willReturn(1L);
            given(refreshTokenRepository.findByMemberId(1L)).willReturn(Optional.of(savedToken));

            assertThatThrownBy(() -> authService.refresh("differentToken"))
                    .isInstanceOf(CustomException.class)
                    .satisfies(e -> assertThat(((CustomException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
        }

    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("성공")
        void success() {
            authService.logout(1L);

            then(refreshTokenRepository).should(times(1)).deleteByMemberId(1L);
        }

    }

}
