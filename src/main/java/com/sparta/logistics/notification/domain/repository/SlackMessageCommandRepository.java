package com.sparta.logistics.notification.domain.repository;

import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlackMessageCommandRepository {
    SlackMessage append(SlackMessage slackMessage);

    Optional<SlackMessage> findById(UUID slackMessageId);

    List<SlackMessage> findAllByStatusIn(List<SlackMessageStatus> statuses);

    void update(SlackMessage slackMessage);

    void delete(UUID slackMessageId, UUID deletedBy);
}
