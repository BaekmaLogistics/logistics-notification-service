package com.sparta.logistics.notification.application.query.usecase;

import com.sparta.logistics.notification.application.query.dto.SlackMessageInfo;

import java.util.UUID;

public interface GetSlackMessageUseCase {
    SlackMessageInfo getMessage(UUID slackMessageId, UUID userId);
}
