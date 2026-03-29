package com.malgn.domain.member.controller;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.TokenResponse;
import com.malgn.domain.member.service.AuthService;
import com.malgn.configure.security.detail.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Auth", description = "인증 API (회원가입, 로그인, 토큰 재발급, 로그아웃)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Operation(summary = "회원가입", description = "이메일, 이름, 닉네임, 비밀번호로 신규 회원을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 (이메일 형식 오류, 비밀번호 불일치 등)"),
            @ApiResponse(responseCode = "409", description = "이메일 또는 닉네임 중복")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Validated @RequestBody MemberRequest.Signup request) {
        authService.signup(request);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다. Access Token과 Refresh Token을 HttpOnly 쿠키로 전달합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    headers = {
                            @Header(name = "Set-Cookie", description = "access_token=...; HttpOnly; Path=/; Max-Age=1800"),
                            @Header(name = "Set-Cookie ", description = "refresh_token=...; HttpOnly; Path=/; Max-Age=604800")
                    }),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public void login(@Validated @RequestBody MemberRequest.Login request, HttpServletResponse response) {
        TokenResponse.Token tokens = authService.login(request);
        addCookie(response, "access_token", tokens.getAccessToken(), accessTokenExpiration / 1000);
        addCookie(response, "refresh_token", tokens.getRefreshToken(), refreshTokenExpiration / 1000);
    }

    @Operation(summary = "Access Token 재발급", description = "로그인 시 발급된 refresh_token 쿠키로 새로운 Access Token을 발급합니다. 요청 바디 없이 브라우저 쿠키가 자동으로 전달됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    headers = @Header(name = "Set-Cookie", description = "access_token=...; HttpOnly; Path=/; Max-Age=1800")),
            @ApiResponse(responseCode = "401", description = "Refresh Token 없음, 만료 또는 유효하지 않은 토큰")
    })
    @PostMapping("/refresh")
    public void refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        String newAccessToken = authService.refresh(refreshToken);
        addCookie(response, "access_token", newAccessToken, accessTokenExpiration / 1000);
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하고 인증 쿠키를 만료시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @SecurityRequirement(name = "Cookie Authentication")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal CustomUserDetails principal, HttpServletResponse response) {
        authService.logout(principal.getMember().getId());
        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");
    }

    private void addCookie(HttpServletResponse response, String name, String value, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
