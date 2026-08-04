package com.sparta.notification.application.query.usecase;

import com.sparta.notification.application.query.dto.SlackMessageInfo;

import java.util.UUID;

public interface GetSlackMessagesUseCase {
    SlackMessageInfo getSlackMessage(UUID slackMessageId, UUID userId);
}
