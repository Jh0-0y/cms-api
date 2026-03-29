package com.malgn.configure.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieManager {

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(accessTokenExpiration / 1000)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearTokenCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

}
