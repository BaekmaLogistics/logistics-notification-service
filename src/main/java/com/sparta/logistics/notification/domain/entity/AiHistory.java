package com.sparta.logistics.notification.domain.entity;

import com.sparta.logistics.notification.domain.model.AiGeneratingStatus;
import com.sparta.logistics.notification.domain.model.AuditInfo;
import com.sparta.logistics.notification.domain.model.DeletionInfo;

import java.util.UUID;

public record AiHistory(
        UUID id,
        String prompt,
        String response,
        AiGeneratingStatus status,
        int retryCount,
        String errorMessage,
        AuditInfo auditInfo,
        DeletionInfo deletionInfo
) {
}