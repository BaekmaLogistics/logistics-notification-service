package com.sparta.logistics.notification.domain.repository;

import com.sparta.logistics.notification.application.query.dto.SearchSlackMessageQuery;
import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SlackMessageQueryRepository {
    Optional<SlackMessageInfo> findByIdAndUserId(UUID slackMessageId, UUID userId);

    Page<SimpleSlackMessageInfo> searchMessages(
            SearchSlackMessageQuery query,
            Pageable pageable,
            UUID userId
    );
}
