package com.sparta.logistics.notification.domain.repository;

import com.sparta.logistics.notification.domain.entity.SlackMessage;

public interface SlackMessageCommandRepository {
    void append(SlackMessage slackMessage);
}
