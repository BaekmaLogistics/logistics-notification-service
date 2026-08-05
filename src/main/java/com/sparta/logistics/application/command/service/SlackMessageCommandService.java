package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.logistics.application.command.usecase.UpdateSlackMessageUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class SlackMessageCommandService implements UpdateSlackMessageUseCase, DeleteSlackMessageUseCase {

    public void save(SendSlackMessageCommand command, UUID userId) {
    }

    @Override
    public void updateMessage(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId) {
    }

    @Override
    public void deleteMessage(UUID slackMessageId, UUID userId) {
    }
}
