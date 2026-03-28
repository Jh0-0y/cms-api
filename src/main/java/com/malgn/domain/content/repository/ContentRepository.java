package com.malgn.domain.content.repository;

import com.malgn.domain.content.entity.Content;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentRepositoryCustom {
}
