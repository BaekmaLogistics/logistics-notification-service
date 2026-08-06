package com.sparta.logistics.notification.domain.repository;

import com.sparta.logistics.notification.domain.entity.SlackMessage;

import java.util.Optional;
import java.util.UUID;

public interface SlackMessageQueryRepository {
    Optional<SlackMessage> findById(UUID slackMessageId);
}
