package com.sparta.logistics.application.command.usecase;

import java.util.UUID;

public interface DeleteSlackMessageUseCase {
    void deleteMessage(UUID slackMessageId, UUID userId);
}
