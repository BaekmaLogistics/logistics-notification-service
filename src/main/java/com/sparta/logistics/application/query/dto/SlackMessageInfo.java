package com.sparta.logistics.application.query.dto;

import com.sparta.logistics.domain.model.SlackMessageStatus;

import java.time.Instant;
import java.util.UUID;

public record SlackMessageInfo(
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
