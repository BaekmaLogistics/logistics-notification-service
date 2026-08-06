package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.domain.entity.SlackMessage;

public interface SlackClient {
    void sendSlackMessage(SlackMessage slackMessage);
}
