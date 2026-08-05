package com.sparta.logistics.presentation.command.dto;

import com.sparta.logistics.application.command.dto.SendSlackMessageCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SendSlackMessageRequest(
        @NotBlank(message = "수신자는 필수 입력값입니다.")
        UUID receiverId,
        @NotBlank(message = "메세지 내용은 필수 입력값입니다.")
        @Size(max = 1024, message = "메세지 내용은 1000자 이하 입력 가능합니다.")
        String content
) {
    public SendSlackMessageCommand toCommand() {
        return new SendSlackMessageCommand(
                this.receiverId,
                this.content
        );
    }
}
