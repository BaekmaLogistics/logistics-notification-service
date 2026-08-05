package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.UpdateSlackMessageCommand;

import java.util.UUID;

public interface UpdateSlackMessageUseCase {
    void updateMessage(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId);
}
