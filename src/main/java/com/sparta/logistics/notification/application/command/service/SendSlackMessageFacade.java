package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.usecase.SendSlackMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class SendSlackMessageFacade implements SendSlackMessageUseCase {
    private final SlackMessageCommandService commandService;
    private final SlackMessageEventProducer eventService;

    @Override
    public void sendMessage(SendSlackMessageCommand command, UUID userId) {
        // 현재 상태를 포함한 SlackMessage Entity 저장, 이벤트 기반 비동기 Slack API 호출

        commandService.save(command, userId); // Transaction
        eventService.produceSlackEvent(command, userId);
    }
}
