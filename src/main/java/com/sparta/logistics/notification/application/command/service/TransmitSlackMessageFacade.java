package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class TransmitSlackMessageFacade implements TransmitSlackMessageUseCase {
    private final SlackMessageCommandService slackMessageCommandService;
    private final SlackClient slackClient;

    @Override
    public void transmit(TransmitSlackMessageCommand command) {
        // TODO: Slack API 호출하여 메세지 전송
    }
}
