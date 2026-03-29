package com.malgn.domain.member.controller;

import com.malgn.configure.security.jwt.JwtTokenProvider;
import com.malgn.domain.member.service.MemberService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("닉네임 변경 유효성 검사")
    class UpdateNicknameValidation {

        @Test
        @WithMockUser
        @DisplayName("실패 - 닉네임 없음")
        void nicknameBlank() throws Exception {
            mockMvc.perform(patch("/api/members/me/nickname")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nickname\": \"\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 닉네임 1자 (최소 2자)")
        void nicknameTooShort() throws Exception {
            mockMvc.perform(patch("/api/members/me/nickname")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nickname\": \"a\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 닉네임 51자 초과")
        void nicknameTooLong() throws Exception {
            String longNickname = "a".repeat(51);
            mockMvc.perform(patch("/api/members/me/nickname")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nickname\": \"" + longNickname + "\"}"))
                    .andExpect(status().isBadRequest());
        }

    }

}
