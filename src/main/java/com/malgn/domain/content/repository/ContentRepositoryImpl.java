package com.malgn.domain.content.repository;

import com.malgn.domain.content.entity.Content;
import com.malgn.domain.content.entity.QContent;
import com.malgn.domain.member.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Content> searchContents(Pageable pageable, String keyword) {
        QContent content = QContent.content;

        BooleanExpression condition = content.isDeleted.isFalse();
        if (StringUtils.hasText(keyword)) {
            condition = condition.and(
                    content.title.containsIgnoreCase(keyword)
                            .or(content.description.containsIgnoreCase(keyword))
            );
        }

        QMember createdBy = new QMember("createdBy");

        List<Content> results = queryFactory
                .selectFrom(content)
                .join(content.createdBy, createdBy).fetchJoin()
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(content.createdDate.desc())
                .fetch();

        Long total = queryFactory
                .select(content.count())
                .from(content)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }

    @Override
    public Optional<Content> findByIdWithCreatedBy(Long id) {
        QContent content = QContent.content;
        QMember createdBy = new QMember("createdBy");

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(content)
                        .join(content.createdBy, createdBy).fetchJoin()
                        .where(content.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public void increaseViewCount(Long id) {
        QContent content = QContent.content;

        queryFactory
                .update(content)
                .set(content.viewCount, content.viewCount.add(1))
                .where(content.id.eq(id))
                .execute();
    }

}
