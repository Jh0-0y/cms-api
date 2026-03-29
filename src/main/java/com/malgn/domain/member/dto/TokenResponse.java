package com.malgn.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenResponse {

    @Schema(description = "로그인 응답 (Access Token + Refresh Token)")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Token {

        @Schema(description = "Access Token (30분 만료)", example = "eyJ...")
        private String accessToken;

        @Schema(description = "Refresh Token (7일 만료)", example = "eyJ...")
        private String refreshToken;

        public static Token of(String accessToken, String refreshToken) {
            Token token = new Token();
            token.accessToken = accessToken;
            token.refreshToken = refreshToken;
            return token;
        }

    }

}
