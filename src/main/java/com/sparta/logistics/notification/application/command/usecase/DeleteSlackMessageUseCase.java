package com.sparta.logistics.notification.application.command.usecase;

import java.util.UUID;

public interface DeleteSlackMessageUseCase {
    void delete(UUID slackMessageId, UUID userId);
}
