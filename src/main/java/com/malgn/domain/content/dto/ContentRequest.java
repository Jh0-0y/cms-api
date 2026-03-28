package com.malgn.domain.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContentRequest {

    @Schema(description = "콘텐츠 생성 요청")
    @Getter
    @Setter
    public static class Create {

        @Schema(description = "제목 (100자 이하)", example = "첫 번째 콘텐츠")
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        private String title;

        @Schema(description = "내용", example = "콘텐츠 내용입니다.")
        private String description;

    }

    @Schema(description = "콘텐츠 수정 요청")
    @Getter
    @Setter
    public static class Update {

        @Schema(description = "제목 (100자 이하)", example = "수정된 제목")
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        private String title;

        @Schema(description = "내용", example = "수정된 내용입니다.")
        private String description;

    }

}
