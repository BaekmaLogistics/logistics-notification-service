package com.sparta.logistics.presentation.command.dto;

import com.sparta.logistics.application.command.dto.UpdateSlackMessageCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSlackMessageRequest(
        @NotBlank(message = "메시지 내용은 필수 입력값입니다.")
        @Size(max = 1000, message = "메세지 내용은 1000자 이하 입력 가능합니다.")
        String content
) {
    public UpdateSlackMessageCommand toCommand() {
        return new UpdateSlackMessageCommand(content);
    }
}
