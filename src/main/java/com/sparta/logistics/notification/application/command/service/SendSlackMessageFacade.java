package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.application.command.usecase.SendSlackMessageUseCase;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class SendSlackMessageFacade implements SendSlackMessageUseCase {
    private final SlackMessageCommandService commandService;
    private final TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;

    @Override
    public void sendMessage(SendSlackMessageCommand command, UUID userId) {
        // TODO: Command 및 userId 검증 필요

        SlackMessage slackMessage = commandService.append(command, userId); // 현재 상태를 포함한 SlackMessage Entity 저장

        transmitSlackMessageEventProducer.produce(command, slackMessage.id(), userId); // 이벤트 기반 비동기 Slack API 호출
    }
}
