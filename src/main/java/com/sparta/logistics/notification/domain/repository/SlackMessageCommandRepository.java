package com.sparta.logistics.notification.domain.repository;

import com.sparta.logistics.notification.domain.entity.SlackMessage;

import java.util.Optional;
import java.util.UUID;

public interface SlackMessageCommandRepository {
    SlackMessage append(SlackMessage slackMessage);

    Optional<SlackMessage> findById(UUID slackMessageId);

    SlackMessage update(SlackMessage slackMessage);

    void delete(UUID slackMessageId, UUID deletedBy);
}
