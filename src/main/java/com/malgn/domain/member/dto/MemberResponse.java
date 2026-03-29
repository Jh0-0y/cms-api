package com.malgn.domain.member.dto;

import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class MemberResponse {

    @Schema(description = "내 정보 응답")
    @Getter
    @Builder
    public static class Me {

        @Schema(description = "회원 ID", example = "1")
        private Long id;

        @Schema(description = "이메일", example = "user@malgn.com")
        private String email;

        @Schema(description = "이름", example = "김맑은")
        private String username;

        @Schema(description = "닉네임", example = "맑은날씨")
        private String nickname;

        @Schema(description = "역할", example = "USER")
        private Role role;

        @Schema(description = "가입일시", example = "2026-03-28T00:00:00")
        private LocalDateTime createdDate;

        public static Me of(Member member) {
            return Me.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .username(member.getUsername())
                    .nickname(member.getNickname())
                    .role(member.getRole())
                    .createdDate(member.getCreatedDate())
                    .build();
        }
    }

}
