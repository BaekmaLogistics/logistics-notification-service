package com.sparta.logistics.notification.presentation.query.dto;

import com.sparta.logistics.notification.application.query.dto.SimpleSlackMessageInfo;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;

import java.time.Instant;
import java.util.UUID;

public record SimpleSlackMessageResponse(
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
    public static SimpleSlackMessageResponse from(SimpleSlackMessageInfo info) {
        return new SimpleSlackMessageResponse(
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
