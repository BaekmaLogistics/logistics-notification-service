package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.SendSlackMessageCommand;

import java.util.UUID;

public interface SendSlackMessageUseCase {
    void sendMessage(SendSlackMessageCommand command, UUID userId);
}
