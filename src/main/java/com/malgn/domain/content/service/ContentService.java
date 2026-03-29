package com.malgn.domain.content.service;

import com.malgn.common.exception.CustomException;
import com.malgn.domain.content.dto.ContentRequest;
import com.malgn.domain.content.dto.ContentResponse;
import com.malgn.domain.content.entity.Content;
import com.malgn.domain.content.repository.ContentRepository;
import com.malgn.domain.member.entity.Member;

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

    @Transactional(readOnly = true)
    public ContentResponse.Page getContents(Pageable pageable, String keyword) {
        return ContentResponse.Page.of(contentRepository.searchContents(pageable, keyword));
    }

    @Transactional
    public ContentResponse.Detail getContent(Long id) {
        Content content = findActiveContent(id);
        contentRepository.increaseViewCount(id);
        return ContentResponse.Detail.of(content);
    }

    @Transactional
    public ContentResponse.Detail createContent(ContentRequest.Create request, Member member) {
        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(member)
                .build();

        contentRepository.save(content);
        log.info("콘텐츠 생성 성공: id={}, createdBy={}", content.getId(), content.getCreatedBy().getNickname());
        return ContentResponse.Detail.of(content);
    }

    @Transactional
    public ContentResponse.Detail updateContent(Long id, ContentRequest.Update request, Member member) {
        Content content = findActiveContent(id);
        content.validatePermission(member);
        content.update(request.getTitle(), request.getDescription(), member);
        log.info("콘텐츠 수정 성공: id={}, lastModifiedBy={}", content.getId(), content.getLastModifiedBy().getNickname());
        return ContentResponse.Detail.of(content);
    }

    @Transactional
    public void deleteContent(Long id, Member member) {
        Content content = findActiveContent(id);
        content.validatePermission(member);
        content.delete();
        log.info("콘텐츠 삭제 성공: id={}", id);
    }

    @Transactional
    public ContentResponse.Detail restoreContent(Long id, Member member) {
        Content content = contentRepository.findByIdWithCreatedBy(id)
                .orElseThrow(() -> {
                    log.debug("콘텐츠 복구 실패 - 존재하지 않는 콘텐츠: id={}", id);
                    return new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
                });
        content.validatePermission(member);
        content.restore();
        log.info("콘텐츠 복구 성공: id={}", id);
        return ContentResponse.Detail.of(content);
    }

    private Content findActiveContent(Long id) {
        Content content = contentRepository.findByIdWithCreatedBy(id)
                .orElseThrow(() -> {
                    log.debug("콘텐츠 조회 실패 - 존재하지 않는 콘텐츠: id={}", id);
                    return new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
                });
        if (content.getIsDeleted()) {
            log.debug("콘텐츠 조회 실패 - 삭제된 콘텐츠: id={}", id);
            throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 콘텐츠입니다.");
        }
        return content;
    }


}
