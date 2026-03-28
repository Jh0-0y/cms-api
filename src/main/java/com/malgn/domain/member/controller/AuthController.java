package com.malgn.domain.member.controller;

import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.TokenRequest;
import com.malgn.domain.member.dto.TokenResponse;
import com.malgn.domain.member.service.AuthService;
import com.malgn.configure.security.detail.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API (회원가입, 로그인, 토큰 재발급, 로그아웃)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

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

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다. Access Token과 Refresh Token을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public TokenResponse.Token login(@Validated @RequestBody MemberRequest.Login request) {
        return authService.login(request);
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token으로 새로운 Access Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 만료 또는 유효하지 않은 토큰")
    })
    @PostMapping("/refresh")
    public TokenResponse.AccessToken refresh(@Validated @RequestBody TokenRequest.Refresh request) {
        return authService.refresh(request);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 Refresh Token을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal CustomUserDetails principal) {
        authService.logout(principal.getMember().getId());
    }

}
