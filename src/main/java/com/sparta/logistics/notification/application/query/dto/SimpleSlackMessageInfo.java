package com.sparta.logistics.notification.application.query.dto;

import com.sparta.logistics.notification.domain.model.SlackMessageStatus;

import java.time.Instant;
import java.util.UUID;

public record SimpleSlackMessageInfo(
        UUID id,
        UUID receiverId,
        UUID senderId,
        String content,
        SlackMessageStatus status,
        int retryCount,
        String errorMessage,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy
) {
}
