package com.sparta.logistics.notification.infrastructure.messaging.event;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;

import java.util.UUID;

public record TransmitSlackMessagePayload(
        UUID slackMessageId,
        UUID receiverId,
        UUID senderId,
        String content
) {
    public static TransmitSlackMessagePayload from(
            SendSlackMessageCommand command,
            UUID slackMessageId
    ) {
        return new TransmitSlackMessagePayload(
                slackMessageId,
                command.receiverId(),
                command.senderId(),
                command.content()
        );
    }

    public TransmitSlackMessageCommand toCommand(UUID actorId) {
        return new TransmitSlackMessageCommand(
                slackMessageId,
                receiverId,
                senderId,
                actorId,
                content
        );
    }
}
