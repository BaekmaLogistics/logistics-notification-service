package com.sparta.logistics.notification.application.command.usecase;

import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UpdateSlackMessageUseCase {
    void update(UUID slackMessageId, UpdateSlackMessageCommand command, UUID userId);

    SlackMessage getSlackMessage(UUID slackMessageId);
}
