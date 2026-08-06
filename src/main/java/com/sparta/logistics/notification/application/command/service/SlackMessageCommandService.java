package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.notification.application.command.usecase.DeleteSlackMessageUseCase;
import com.sparta.logistics.notification.application.command.usecase.UpdateSlackMessageUseCase;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class SlackMessageCommandService implements UpdateSlackMessageUseCase, DeleteSlackMessageUseCase {
    private final SlackMessageCommandRepository slackMessageCommandRepository;

    @Transactional
    public SlackMessage append(SendSlackMessageCommand command, UUID userId) {
        SlackMessage slackMessage = SlackMessage.create(
                command.receiverId(),
                userId,
                command.content()
        );

        return slackMessageCommandRepository.append(slackMessage);
    }

    @Override
    @Transactional
    public void update(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId) {
    }

    @Override
    @Transactional
    public void delete(UUID slackMessageId, UUID userId) {
    }

    @Override
    @Transactional(readOnly = true)
    public SlackMessage getSlackMessage(UUID slackMessageId) {
        return null;
    }
}
