package com.malgn.domain.content.controller;

import com.malgn.domain.content.service.ContentService;
import com.malgn.jwt.JwtTokenProvider;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentController.class)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContentService contentService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("콘텐츠 생성 유효성 검사")
    class CreateValidation {

        @Test
        @WithMockUser
        @DisplayName("실패 - 제목 없음")
        void titleBlank() throws Exception {
            mockMvc.perform(post("/api/contents")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\": \"\", \"description\": \"내용\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 제목 101자 초과")
        void titleTooLong() throws Exception {
            String longTitle = "a".repeat(101);
            mockMvc.perform(post("/api/contents")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\": \"" + longTitle + "\", \"description\": \"내용\"}"))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    @DisplayName("콘텐츠 수정 유효성 검사")
    class UpdateValidation {

        @Test
        @WithMockUser
        @DisplayName("실패 - 제목 없음")
        void titleBlank() throws Exception {
            mockMvc.perform(put("/api/contents/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\": \"\", \"description\": \"내용\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("실패 - 제목 101자 초과")
        void titleTooLong() throws Exception {
            String longTitle = "a".repeat(101);
            mockMvc.perform(put("/api/contents/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\": \"" + longTitle + "\", \"description\": \"내용\"}"))
                    .andExpect(status().isBadRequest());
        }

    }

}
