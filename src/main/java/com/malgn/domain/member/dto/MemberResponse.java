package com.malgn.domain.member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Token {

        private String accessToken;
        private String refreshToken;

        public static Token of(String accessToken, String refreshToken) {
            Token token = new Token();
            token.accessToken = accessToken;
            token.refreshToken = refreshToken;
            return token;
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AccessToken {

        private String accessToken;

        public static AccessToken of(String accessToken) {
            AccessToken response = new AccessToken();
            response.accessToken = accessToken;
            return response;
        }

    }

}
