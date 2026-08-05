package com.sparta.logistics.presentation.command.dto;

import com.sparta.logistics.application.command.dto.UpdateSlackMessageCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateSlackMessageRequest(
        @NotBlank(message = "메시지 내용은 필수 입력값입니다.")
        String content
) {
    public UpdateSlackMessageCommand toCommand() {
        return new UpdateSlackMessageCommand(content);
    }
}
