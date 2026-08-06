package com.sparta.logistics.notification.domain.entity;

import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.model.AuditInfo;
import com.sparta.logistics.notification.domain.model.DeletionInfo;
import com.sparta.logistics.notification.domain.model.SlackMessageStatus;

import java.time.Instant;
import java.util.UUID;

public record SlackMessage(
        UUID id,
        UUID receiverId,
        UUID senderId,
        String content,
        SlackMessageStatus status,
        int retryCount,
        String errorMessage,
        AuditInfo auditInfo,
        DeletionInfo deletionInfo
) {
    public static SlackMessage create(
            UUID receiverId, UUID senderId, String content
    ) {
        validate(receiverId, senderId, content);
        verifyContent(content);

        return new SlackMessage(
                null,
                receiverId,
                senderId,
                content,
                SlackMessageStatus.PENDING,
                0,
                null,
                new AuditInfo(
                        Instant.now(), senderId, null, null
                ),
                null
        );
    }

    public SlackMessage complete(UUID updatedBy) {
        return transitionTo(SlackMessageStatus.SUCCESS, this.errorMessage, updatedBy);
    }

    public SlackMessage fail(String errorMessage, UUID updatedBy) {
        return transitionTo(SlackMessageStatus.FAILED, errorMessage, updatedBy);
    }

    public SlackMessage retry(UUID updatedBy) {
        return transitionTo(SlackMessageStatus.RETRYING, this.errorMessage, updatedBy);
    }

    private SlackMessage transitionTo(SlackMessageStatus targetStatus, String errorMessage, UUID updatedBy) {
        if (!status.canTransitionTo(targetStatus)) {
            throw new ApiException(
                    ErrorResponseCode.INVALID_REQUEST,
                    String.format("[%s] 상태에서 [%s] 상태로 변경할 수 없습니다.", this.status, targetStatus)
            );
        }

        int newRetryCount = targetStatus == SlackMessageStatus.RETRYING ? this.retryCount + 1
                : this.retryCount;

        String newErrorMessage = errorMessage != null ? errorMessage : this.errorMessage;

        AuditInfo newAuditInfo = new AuditInfo(
                this.auditInfo != null ? this.auditInfo.createdAt() : Instant.now(),
                this.auditInfo != null ? this.auditInfo.createdBy() : null,
                Instant.now(),
                updatedBy != null ? updatedBy : (this.auditInfo != null ? this.auditInfo.updatedBy() : null)
        );

        return new SlackMessage(
                this.id,
                this.receiverId,
                this.senderId,
                this.content,
                targetStatus,
                newRetryCount,
                newErrorMessage,
                newAuditInfo,
                this.deletionInfo
        );
    }

    private static void validate(UUID receiverId, UUID senderId, String content) {
        if (receiverId == null || senderId == null || content == null || content.
                isBlank()) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "수신자, 발신자, 메시지 내용은 필수입니다.");
        }
    }

    private static void verifyContent(String content) {
        if (content.length() > 1000) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "메세지 내용은 1000자 이하 입력 가능합니다.");
        }
    }
}
