package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.dto.SendSlackMessageCommand;
import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SendSlackMessageFacadeTest {

    @InjectMocks
    private SendSlackMessageFacade sendSlackMessageFacade;

    @Mock
    private SlackMessageCommandService commandService;

    @Mock
    private TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;

    @Test
    @DisplayName("Slack 메시지 전송 요청 - 메시지 저장을 수행하고 이벤트를 발행한다")
    void sendMessage_success() {
        // given
        UUID receiverId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID slackMessageId = UUID.randomUUID();
        String content = "테스트 메시지 내용";

        SendSlackMessageCommand command = new SendSlackMessageCommand(receiverId, senderId, content);
        SlackMessage savedSlackMessage = mock(SlackMessage.class);

        given(savedSlackMessage.id()).willReturn(slackMessageId);
        given(commandService.append(receiverId, senderId, content)).willReturn(savedSlackMessage);

        // when
        sendSlackMessageFacade.sendMessage(command, actorId);

        // then
        then(commandService).should().append(receiverId, senderId, content);
        then(transmitSlackMessageEventProducer).should().produce(slackMessageId, actorId);
    }
}
