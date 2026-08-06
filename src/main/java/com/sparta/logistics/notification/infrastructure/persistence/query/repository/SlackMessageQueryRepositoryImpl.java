package com.sparta.logistics.notification.infrastructure.persistence.query.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sparta.logistics.notification.infrastructure.persistence.command.entity.QSlackMessageJpaEntity.slackMessageJpaEntity;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackMessageQueryRepositoryImpl implements SlackMessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<SlackMessageInfo> findByIdAndUserId(UUID slackMessageId, UUID userId) {
        BooleanExpression condition = slackMessageJpaEntity.id.eq(slackMessageId)
                .and(slackMessageJpaEntity.deletedAt.isNull())
                .and(slackMessageJpaEntity.receiverId.eq(userId).or(slackMessageJpaEntity.senderId.eq(userId)));

        SlackMessageInfo info = queryFactory
                .select(Projections.constructor(SlackMessageInfo.class,
                        slackMessageJpaEntity.id,
                        slackMessageJpaEntity.receiverId,
                        slackMessageJpaEntity.senderId,
                        slackMessageJpaEntity.content,
                        slackMessageJpaEntity.status,
                        slackMessageJpaEntity.retryCount,
                        slackMessageJpaEntity.errorMessage,
                        slackMessageJpaEntity.createdAt,
                        slackMessageJpaEntity.createdBy,
                        slackMessageJpaEntity.updatedAt,
                        slackMessageJpaEntity.updatedBy
                ))
                .from(slackMessageJpaEntity)
                .where(condition)
                .fetchOne();

        return Optional.ofNullable(info);
    }

    @Override
    public Page<SimpleSlackMessageInfo> searchMessages(SearchSlackMessageQuery query, Pageable pageable, UUID userId) {
        BooleanExpression condition = slackMessageJpaEntity.deletedAt.isNull()
                .and(slackMessageJpaEntity.receiverId.eq(userId).or(slackMessageJpaEntity.senderId.eq(userId)))
                .and(eqReceiverId(query.receiverId()))
                .and(eqSenderId(query.senderId()))
                .and(containsKeyword(query.keyword()))
                .and(betweenCreatedAt(query.startDate(), query.endDate()));

        List<SimpleSlackMessageInfo> content = queryFactory
                .select(Projections.constructor(SimpleSlackMessageInfo.class,
                        slackMessageJpaEntity.id,
                        slackMessageJpaEntity.receiverId,
                        slackMessageJpaEntity.senderId,
                        slackMessageJpaEntity.content,
                        slackMessageJpaEntity.status,
                        slackMessageJpaEntity.retryCount,
                        slackMessageJpaEntity.errorMessage,
                        slackMessageJpaEntity.createdAt,
                        slackMessageJpaEntity.createdBy,
                        slackMessageJpaEntity.updatedAt,
                        slackMessageJpaEntity.updatedBy
                ))
                .from(slackMessageJpaEntity)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(slackMessageJpaEntity.count())
                .from(slackMessageJpaEntity)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression eqReceiverId(UUID receiverId) {
        return receiverId != null ? slackMessageJpaEntity.receiverId.eq(receiverId) : null;
    }

    private BooleanExpression eqSenderId(UUID senderId) {
        return senderId != null ? slackMessageJpaEntity.senderId.eq(senderId) : null;
    }

    private BooleanExpression containsKeyword(String keyword) {
        return keyword != null && !keyword.isBlank() ? slackMessageJpaEntity.content.contains(keyword) : null;
    }

    private BooleanExpression betweenCreatedAt(Instant startDate, Instant endDate) {
        if (startDate != null && endDate != null) {
            return slackMessageJpaEntity.createdAt.between(startDate, endDate);
        } else if (startDate != null) {
            return slackMessageJpaEntity.createdAt.goe(startDate);
        } else if (endDate != null) {
            return slackMessageJpaEntity.createdAt.loe(endDate);
        }
        return null;
    }
}
