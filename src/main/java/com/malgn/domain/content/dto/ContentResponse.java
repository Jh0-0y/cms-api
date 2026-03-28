package com.malgn.domain.content.dto;

import com.malgn.domain.content.entity.Content;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ContentResponse {

    @Schema(description = "콘텐츠 상세 응답")
    @Getter
    @Builder
    public static class Detail {

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long id;

        @Schema(description = "제목", example = "첫 번째 콘텐츠")
        private String title;

        @Schema(description = "내용", example = "콘텐츠 내용입니다.")
        private String description;

        @Schema(description = "조회수", example = "42")
        private Long viewCount;

        @Schema(description = "작성자 닉네임", example = "hong")
        private String createdBy;

        @Schema(description = "생성일시", example = "2026-03-27T00:00:00")
        private LocalDateTime createdDate;

        @Schema(description = "최종 수정자 닉네임", example = "hong")
        private String lastModifiedBy;

        @Schema(description = "최종 수정일시", example = "2026-03-27T00:00:00")
        private LocalDateTime lastModifiedDate;

        public static Detail of(Content content) {
            return Detail.builder()
                    .id(content.getId())
                    .title(content.getTitle())
                    .description(content.getDescription())
                    .viewCount(content.getViewCount())
                    .createdBy(content.getCreatedBy())
                    .createdDate(content.getCreatedDate())
                    .lastModifiedBy(content.getLastModifiedBy())
                    .lastModifiedDate(content.getLastModifiedDate())
                    .build();
        }

    }

    @Schema(description = "콘텐츠 목록 아이템")
    @Getter
    @Builder
    public static class Summary {

        @Schema(description = "콘텐츠 ID", example = "1")
        private Long id;

        @Schema(description = "제목", example = "첫 번째 콘텐츠")
        private String title;

        @Schema(description = "조회수", example = "42")
        private Long viewCount;

        @Schema(description = "작성자 닉네임", example = "hong")
        private String createdBy;

        @Schema(description = "생성일시", example = "2026-03-27T00:00:00")
        private LocalDateTime createdDate;

        public static Summary of(Content content) {
            return Summary.builder()
                    .id(content.getId())
                    .title(content.getTitle())
                    .viewCount(content.getViewCount())
                    .createdBy(content.getCreatedBy())
                    .createdDate(content.getCreatedDate())
                    .build();
        }

    }

    @Schema(description = "콘텐츠 목록 페이징 응답")
    @Getter
    @Builder
    public static class Page {

        @Schema(description = "콘텐츠 목록")
        private List<Summary> content;

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        private int page;

        @Schema(description = "페이지 크기", example = "10")
        private int size;

        @Schema(description = "전체 콘텐츠 수", example = "100")
        private long totalElements;

        @Schema(description = "전체 페이지 수", example = "10")
        private int totalPages;

        public static Page of(org.springframework.data.domain.Page<Content> pageResult) {
            return Page.builder()
                    .content(pageResult.getContent().stream().map(Summary::of).toList())
                    .page(pageResult.getNumber())
                    .size(pageResult.getSize())
                    .totalElements(pageResult.getTotalElements())
                    .totalPages(pageResult.getTotalPages())
                    .build();
        }

    }

}
