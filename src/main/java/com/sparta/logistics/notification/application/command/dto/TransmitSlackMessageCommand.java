package com.sparta.logistics.notification.application.command.dto;

import java.util.UUID;

public record TransmitSlackMessageCommand(
        UUID slackMessageId,
        UUID receiverId,
        UUID senderId,
        UUID actorId,
        String content
) {
}
