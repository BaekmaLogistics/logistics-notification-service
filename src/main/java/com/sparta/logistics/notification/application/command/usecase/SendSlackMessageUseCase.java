package com.sparta.logistics.notification.application.command.usecase;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;

import java.util.UUID;

public interface SendSlackMessageUseCase {
    void sendMessage(SendSlackMessageCommand command, UUID userId);
}
