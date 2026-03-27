package com.malgn.domain.member.dto;

import lombok.Getter;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberRequest {

    @Getter
    public static class Signup {

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "이름은 필수입니다.")
        @Size(min= 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
        private String username;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min= 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
        private String nickname;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String passwordConfirm;

        @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
        public boolean isPasswordConfirmValid() {
            return password != null && password.equals(passwordConfirm);
        }

    }

    @Getter
    public static class Login {

        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;

    }

    @Getter
    public static class Refresh {

        @NotBlank(message = "Refresh Token은 필수입니다.")
        private String refreshToken;

    }

}
