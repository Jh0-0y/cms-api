package com.malgn.domain.content.entity;

import com.malgn.domain.member.entity.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false, length = 50)
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(length = 50)
    private String lastModifiedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Content(String title, String description, String createdBy, Member member) {
        this.title = title;
        this.description = description;
        this.viewCount = 0L;
        this.isDeleted = false;
        this.createdDate = LocalDateTime.now();
        this.createdBy = createdBy;
        this.member = member;
    }

    public void update(String title, String description, String nickname) {
        this.title = title;
        this.description = description;
        this.lastModifiedDate = LocalDateTime.now();
        this.lastModifiedBy = nickname;
    }

    public void delete() {
        this.isDeleted = true;
    }

}
