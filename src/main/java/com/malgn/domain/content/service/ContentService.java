package com.malgn.domain.content.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.content.dto.ContentRequest;
import com.malgn.domain.content.dto.ContentResponse;
import com.malgn.domain.content.entity.Content;
import com.malgn.domain.content.repository.ContentRepository;
import com.malgn.domain.member.entity.Member;
import com.malgn.domain.member.repository.MemberRepository;
import com.malgn.configure.security.detail.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ContentResponse.Page getContents(Pageable pageable, String keyword) {
        return ContentResponse.Page.of(contentRepository.searchContents(pageable, keyword));
    }

    @Transactional
    public ContentResponse.Detail getContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("콘텐츠 조회 실패 - 존재하지 않는 콘텐츠: id={}", id);
                    return new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
                });

        if (content.getIsDeleted()) {
            log.debug("콘텐츠 조회 실패 - 삭제된 콘텐츠: id={}", id);
            throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
        }

        contentRepository.increaseViewCount(id);

        return ContentResponse.Detail.of(content);
    }

    @Transactional
    public ContentResponse.Detail createContent(ContentRequest.Create request, CustomUserDetails principal) {
        Member member = memberRepository.findById(principal.getMemberId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(principal.getNickname())
                .member(member)
                .build();

        contentRepository.save(content);
        log.info("콘텐츠 생성 성공: id={}, createdBy={}", content.getId(), content.getCreatedBy());
        return ContentResponse.Detail.of(content);
    }

    @Transactional
    public ContentResponse.Detail updateContent(Long id, ContentRequest.Update request, CustomUserDetails principal) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("콘텐츠 수정 실패 - 존재하지 않는 콘텐츠: id={}", id);
                    return new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
                });

        if (content.getIsDeleted()) {
            log.debug("콘텐츠 수정 실패 - 삭제된 콘텐츠: id={}", id);
            throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
        }

        checkPermission(content, principal, "수정");

        content.update(request.getTitle(), request.getDescription(), principal.getNickname());
        log.info("콘텐츠 수정 성공: id={}, lastModifiedBy={}", content.getId(), content.getLastModifiedBy());
        return ContentResponse.Detail.of(content);
    }

    @Transactional
    public void deleteContent(Long id, CustomUserDetails principal) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> {
                    log.debug("콘텐츠 삭제 실패 - 존재하지 않는 콘텐츠: id={}", id);
                    return new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
                });

        if (content.getIsDeleted()) {
            log.debug("콘텐츠 삭제 실패 - 이미 삭제된 콘텐츠: id={}", id);
            throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
        }

        checkPermission(content, principal, "삭제");

        content.delete();
        log.info("콘텐츠 삭제 성공: id={}", id);
    }

    private void checkPermission(Content content, CustomUserDetails principal, String action) {
        boolean isOwner = content.getMember().getId().equals(principal.getMemberId());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isOwner && !isAdmin) {
            log.debug("콘텐츠 {} 실패 - 권한 없음: contentId={}, memberId={}", action, content.getId(), principal.getMemberId());
            throw new CustomException(HttpStatus.FORBIDDEN, action + " 권한이 없습니다.");
        }
    }

}
