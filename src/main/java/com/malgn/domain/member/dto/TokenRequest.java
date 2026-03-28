package com.malgn.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

public class TokenRequest {

    @Schema(description = "Access Token 재발급 요청")
    @Getter
    public static class Refresh {

        @Schema(description = "Refresh Token", example = "eyJ...")
        @NotBlank(message = "Refresh Token은 필수입니다.")
        private String refreshToken;

    }

}
