package com.sparta.notification.application.command.service;

import com.sparta.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.notification.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.notification.application.command.usecase.UpdateSlackMessageUseCase;
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
