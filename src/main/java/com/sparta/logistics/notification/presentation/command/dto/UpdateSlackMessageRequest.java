package com.sparta.logistics.notification.presentation.command.dto;

import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
import jakarta.validation.constraints.Size;

public record UpdateSlackMessageRequest(
        @Size(max = 64, message = "슬랙 ID 값은 64자 이하 입력 가능합니다.")
        String receiverSlackId,
        @Size(max = 64, message = "슬랙 ID 값은 64자 이하 입력 가능합니다.")
        String senderSlackId,
        @Size(max = 1000, message = "메세지 내용은 1000자 이하 입력 가능합니다.")
        String content
) {
    public UpdateSlackMessageCommand toCommand() {
        return new UpdateSlackMessageCommand(receiverSlackId, senderSlackId, content);
    }
}
