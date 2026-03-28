package com.malgn.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

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

    @Schema(description = "Access Token 재발급 응답")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AccessToken {

        @Schema(description = "새로 발급된 Access Token (30분 만료)", example = "eyJ...")
        private String accessToken;

        public static AccessToken of(String accessToken) {
            AccessToken response = new AccessToken();
            response.accessToken = accessToken;
            return response;
        }

    }

}
