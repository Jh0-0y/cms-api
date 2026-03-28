package com.malgn.domain.content.repository;

import com.malgn.domain.content.entity.Content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepositoryCustom {

    Page<Content> searchContents(Pageable pageable, String keyword);

    void increaseViewCount(Long id);

}
