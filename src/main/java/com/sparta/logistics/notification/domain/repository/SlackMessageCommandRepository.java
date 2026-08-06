package com.sparta.logistics.notification.domain.repository;

import com.sparta.logistics.notification.domain.entity.SlackMessage;

public interface SlackMessageCommandRepository {
    SlackMessage append(SlackMessage slackMessage);
}
