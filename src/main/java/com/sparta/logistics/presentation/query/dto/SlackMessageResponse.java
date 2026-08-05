package com.sparta.logistics.presentation.query.dto;

import com.sparta.logistics.application.query.dto.SlackMessageInfo;
import com.sparta.logistics.domain.model.SlackMessageStatus;

import java.time.Instant;
import java.util.UUID;

public record SlackMessageResponse(
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
    public static SlackMessageResponse from(SlackMessageInfo info) {
        return new SlackMessageResponse(
                info.id(),
                info.receiverId(),
                info.senderId(),
                info.content(),
                info.status(),
                info.retryCount(),
                info.errorMessage(),
                info.createdAt(),
                info.createdBy(),
                info.updatedAt(),
                info.updatedBy()
        );
    }
}
