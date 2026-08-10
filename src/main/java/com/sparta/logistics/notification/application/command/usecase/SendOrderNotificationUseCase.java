package com.sparta.logistics.notification.application.command.usecase;

import com.sparta.logistics.notification.application.command.dto.SendOrderNotificationCommand;

public interface SendOrderNotificationUseCase {
    void send(SendOrderNotificationCommand command);
}
