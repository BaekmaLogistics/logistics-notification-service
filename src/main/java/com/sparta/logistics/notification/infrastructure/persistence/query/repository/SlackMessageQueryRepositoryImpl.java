package com.sparta.logistics.notification.infrastructure.persistence.query.repository;

import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.notification.domain.repository.SlackMessageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SlackMessageQueryRepositoryImpl implements SlackMessageQueryRepository {
    private final SlackMessageQueryJpaRepository slackMessageQueryJpaRepository;

    @Override
    public Optional<SlackMessageInfo> findByIdAndUserId(UUID slackMessageId, UUID userId) {
        return Optional.empty();
    }

    @Override
    public Page<SimpleSlackMessageInfo> searchMessages(SearchSlackMessageQuery query, Pageable pageable, UUID userId) {
        return null;
    }
}
