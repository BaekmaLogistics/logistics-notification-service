package com.sparta.notification.application.command.usecase;

import com.sparta.notification.application.command.dto.UpdateSlackMessageCommand;

import java.util.UUID;

public interface UpdateSlackMessageUseCase {
    void updateMessage(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId);
}
