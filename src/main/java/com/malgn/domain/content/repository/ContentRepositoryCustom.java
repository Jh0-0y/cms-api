package com.malgn.domain.content.repository;

import com.malgn.domain.content.entity.Content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContentRepositoryCustom {

    Page<Content> searchContents(Pageable pageable, String keyword);

    Optional<Content> findByIdWithCreatedBy(Long id);

    void increaseViewCount(Long id);

}
