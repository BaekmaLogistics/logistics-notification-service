package com.sparta.logistics.notification.infrastructure.persistence.query.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
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
    public Optional<SlackMessageInfo> findById(UUID slackMessageId) {
        BooleanExpression condition = slackMessageJpaEntity.id.eq(slackMessageId)
                .and(slackMessageJpaEntity.deletedAt.isNull());

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
    public Page<SimpleSlackMessageInfo> searchMessages(SearchSlackMessageQuery query, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);

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
                .where(
                        slackMessageJpaEntity.deletedAt.isNull(),
                        eqReceiverId(query.receiverId()),
                        eqSenderId(query.senderId()),
                        containsKeyword(query.keyword()),
                        betweenCreatedAt(query.startDate(), query.endDate())
                )
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(slackMessageJpaEntity.count())
                .from(slackMessageJpaEntity)
                .where(
                        slackMessageJpaEntity.deletedAt.isNull(),
                        eqReceiverId(query.receiverId()),
                        eqSenderId(query.senderId()),
                        containsKeyword(query.keyword()),
                        betweenCreatedAt(query.startDate(), query.endDate())
                )
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
        // TODO: 메세지 양이 많아질 경우 ElasticSearch 도입 고려
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

    private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (pageable.getSort().isUnsorted()) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, slackMessageJpaEntity.createdAt));
            return orderSpecifiers;
        }

        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;

            if ("updatedAt".equals(sortOrder.getProperty())) {
                orderSpecifiers.add(new OrderSpecifier<>(direction, slackMessageJpaEntity.updatedAt));
            } else {
                orderSpecifiers.add(new OrderSpecifier<>(direction, slackMessageJpaEntity.createdAt));
            }
        }
        return orderSpecifiers;
    }
}
