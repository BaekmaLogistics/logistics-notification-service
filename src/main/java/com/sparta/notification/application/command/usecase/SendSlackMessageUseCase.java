package com.sparta.notification.application.command.usecase;

import com.sparta.notification.application.command.dto.SendSlackMessageCommand;

import java.util.UUID;

public interface SendSlackMessageUseCase {
    void sendMessage(SendSlackMessageCommand command, UUID userId);
}
