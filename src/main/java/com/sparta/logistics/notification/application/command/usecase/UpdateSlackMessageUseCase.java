package com.sparta.logistics.notification.application.command.usecase;

import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;

import java.util.UUID;

public interface UpdateSlackMessageUseCase {
    void update(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId);
}
