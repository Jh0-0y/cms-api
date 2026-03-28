package com.malgn.domain.content.dto;

import com.malgn.domain.content.entity.Content;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ContentResponse {

    @Getter
    @Builder
    public static class Detail {

        private Long id;
        private String title;
        private String description;
        private Long viewCount;
        private String createdBy;
        private LocalDateTime createdDate;
        private String lastModifiedBy;
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

    @Getter
    @Builder
    public static class Summary {

        private Long id;
        private String title;
        private Long viewCount;
        private String createdBy;
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

    @Getter
    @Builder
    public static class Page {

        private List<Summary> content;
        private int page;
        private int size;
        private long totalElements;
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
