package com.sparta.logistics.application.query.usecase;

import com.sparta.logistics.application.query.dto.SlackMessageInfo;

import java.util.UUID;

public interface GetSlackMessageUseCase {
    SlackMessageInfo getMessage(UUID slackMessageId, UUID userId);
}
