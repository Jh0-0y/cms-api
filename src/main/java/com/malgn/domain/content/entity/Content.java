package com.malgn.domain.content.entity;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.entity.Role;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private Member createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by")
    private Member lastModifiedBy;

    @Builder
    public Content(String title, String description, Member createdBy) {
        this.title = title;
        this.description = description;
        this.viewCount = 0L;
        this.isDeleted = false;
        this.createdBy = createdBy;
    }

    public void update(String title, String description, Member lastModifiedBy) {
        this.title = title;
        this.description = description;
        this.lastModifiedBy = lastModifiedBy;
    }

    public void validatePermission(Member member) {
        boolean isOwner = this.createdBy.getId().equals(member.getId());
        boolean isAdmin = member.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin)
            throw new CustomException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
    }

    public void delete() {
        if (this.isDeleted)
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 삭제된 콘텐츠입니다.");
        this.isDeleted = true;
    }

    public void restore() {
        if (!this.isDeleted)
            throw new CustomException(HttpStatus.BAD_REQUEST, "삭제되지 않은 콘텐츠입니다.");
        this.isDeleted = false;
    }

}
