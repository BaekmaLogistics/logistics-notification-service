package com.sparta.logistics.notification.infrastructure.messaging.event;

import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;

import java.util.UUID;

public record TransmitSlackMessagePayload(
        UUID slackMessageId
) {
    public TransmitSlackMessageCommand toCommand(UUID actorId) {
        return new TransmitSlackMessageCommand(
                slackMessageId,
                actorId
        );
    }
}
