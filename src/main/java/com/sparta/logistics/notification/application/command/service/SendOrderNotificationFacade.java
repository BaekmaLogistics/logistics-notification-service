package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.dto.SendOrderNotificationCommand;
import com.sparta.logistics.notification.application.command.usecase.SendOrderNotificationUseCase;
import org.springframework.stereotype.Service;

@Service
public class SendOrderNotificationFacade implements SendOrderNotificationUseCase {
    @Override
    public void send(SendOrderNotificationCommand command) {

    }
}
