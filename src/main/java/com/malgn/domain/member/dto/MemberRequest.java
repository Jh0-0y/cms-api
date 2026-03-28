package com.malgn.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberRequest {

    @Schema(description = "회원가입 요청")
    @Getter
    public static class Signup {

        @Schema(description = "이메일", example = "user@malgn.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @Schema(description = "이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min= 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
        private String username;

        @Schema(description = "닉네임 (화면 표시용, 중복 불가)", example = "hong")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min= 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
        private String nickname;

        @Schema(description = "비밀번호 (8자 이상)", example = "password123")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        private String password;

        @Schema(description = "비밀번호 확인", example = "password123")
        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String passwordConfirm;

        @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
        public boolean isPasswordConfirmValid() {
            return password != null && password.equals(passwordConfirm);
        }

    }

    @Schema(description = "로그인 요청")
    @Getter
    public static class Login {

        @Schema(description = "이메일", example = "user@malgn.com")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @Schema(description = "비밀번호", example = "user123")
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;

    }

}
