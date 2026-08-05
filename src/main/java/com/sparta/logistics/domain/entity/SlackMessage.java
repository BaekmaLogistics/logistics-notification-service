package com.sparta.logistics.domain.entity;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.model.AuditInfo;
import com.sparta.logistics.domain.model.DeletionInfo;
import com.sparta.logistics.domain.model.SlackMessageStatus;

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
                null,
                null
        );
    }

    public SlackMessage complete() {
        return transitionTo(SlackMessageStatus.SUCCESS, this.errorMessage);
    }

    public SlackMessage fail(String errorMessage) {
        return transitionTo(SlackMessageStatus.FAILED, errorMessage);
    }

    public SlackMessage retry() {
        return transitionTo(SlackMessageStatus.RETRYING, this.errorMessage);
    }

    private SlackMessage transitionTo(SlackMessageStatus targetStatus, String errorMessage) {
        if (!status.canTransitionTo(targetStatus)) {
            throw new ApiException(
                    ErrorResponseCode.INVALID_REQUEST,
                    String.format("[%s] 상태에서 [%s] 상태로 변경할 수 없습니다.", this.status, targetStatus)
            );
        }

        int newRetryCount = targetStatus == SlackMessageStatus.RETRYING ? this.retryCount + 1
                : this.retryCount;

        String newErrorMessage = errorMessage != null ? errorMessage : this.errorMessage;

        return new SlackMessage(
                this.id,
                this.receiverId,
                this.senderId,
                this.content,
                targetStatus,
                newRetryCount,
                newErrorMessage,
                this.auditInfo,
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
