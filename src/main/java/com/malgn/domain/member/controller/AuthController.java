package com.malgn.domain.member.controller;

import com.malgn.domain.member.dto.MemberRequest;
import com.malgn.domain.member.dto.MemberResponse;
import com.malgn.domain.member.service.AuthService;
import com.malgn.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Validated @RequestBody MemberRequest.Signup request) {
        authService.signup(request);
    }

    @PostMapping("/login")
    public MemberResponse.Token login(@Validated @RequestBody MemberRequest.Login request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public MemberResponse.AccessToken refresh(@Validated @RequestBody MemberRequest.Refresh request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal CustomUserDetails principal) {
        authService.logout(principal.getMemberId());
    }

}
