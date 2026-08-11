package com.sparta.logistics.notification.application.command.usecase;

import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;

public interface TransmitSlackMessageUseCase {
    void transmit(TransmitSlackMessageCommand command);
}
