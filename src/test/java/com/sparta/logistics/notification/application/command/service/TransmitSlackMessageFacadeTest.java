package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TransmitSlackMessageFacadeTest {

    @InjectMocks
    private TransmitSlackMessageFacade transmitSlackMessageFacade;

    @Mock
    private SlackMessageCommandService slackMessageCommandService;

    @Mock
    private SlackClient slackClient;

    @Mock
    private TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;

    @Test
    @DisplayName("Slack 메시지 전달 - 성공 시 processing -> success 처리")
    void transmit_success() {
        // given
        UUID slackMessageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        TransmitSlackMessageCommand command = new TransmitSlackMessageCommand(slackMessageId, actorId);
        SlackMessage slackMessage = mock(SlackMessage.class);

        given(slackMessageCommandService.updateStatusToProcessing(slackMessageId, actorId)).willReturn(slackMessage);

        // when
        transmitSlackMessageFacade.transmit(command);

        // then
        then(slackClient).should().transmitSlackMessage(slackMessage);
        then(slackMessageCommandService).should().updateStatusToSuccess(slackMessageId, actorId);
    }

    @Test
    @DisplayName("Slack 메시지 전달 - 외부에러 발생 시 재시도 횟수 미달이면 retrying 상태 변경 및 이벤트 재발행")
    void transmit_retry() {
        // given
        UUID slackMessageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        TransmitSlackMessageCommand command = new TransmitSlackMessageCommand(slackMessageId, actorId);
        SlackMessage slackMessage = mock(SlackMessage.class);

        given(slackMessage.id()).willReturn(slackMessageId);
        given(slackMessage.retryCount()).willReturn(1);
        given(slackMessageCommandService.updateStatusToProcessing(slackMessageId, actorId)).willReturn(slackMessage);
        willThrow(new RuntimeException("Slack API Call Failed")).given(slackClient).transmitSlackMessage(slackMessage);

        // when
        transmitSlackMessageFacade.transmit(command);

        // then
        then(slackMessageCommandService).should().updateStatusToRetrying(slackMessageId, actorId);
        then(transmitSlackMessageEventProducer).should().produce(slackMessageId, actorId);
    }

    @Test
    @DisplayName("Slack 메시지 전달 - 최대 재시도 횟수 초과 시 failed 상태 변경")
    void transmit_failed() {
        // given
        UUID slackMessageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        TransmitSlackMessageCommand command = new TransmitSlackMessageCommand(slackMessageId, actorId);
        SlackMessage slackMessage = mock(SlackMessage.class);

        given(slackMessage.retryCount()).willReturn(3);
        given(slackMessageCommandService.updateStatusToProcessing(slackMessageId, actorId)).willReturn(slackMessage);
        willThrow(new RuntimeException("Slack API Call Failed")).given(slackClient).transmitSlackMessage(slackMessage);

        // when
        transmitSlackMessageFacade.transmit(command);

        // then
        then(slackMessageCommandService).should().updateStatusToFailed(slackMessageId, "Slack API Call Failed", actorId);
    }

    @Test
    @DisplayName("Slack 메시지 전달 - updateStatusToProcessing 실패 예외 발생 시 ApiException 던지고 슬랙 전송 안 함")
    void transmit_processingException_shouldCatchAndNotRetry() {
        // given
        UUID slackMessageId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        TransmitSlackMessageCommand command = new TransmitSlackMessageCommand(slackMessageId, actorId);

        willThrow(new ApiException(ErrorResponseCode.INVALID_REQUEST, "상태 변경 불가"))
                .given(slackMessageCommandService).updateStatusToProcessing(slackMessageId, actorId);

        // when & then
        assertThatThrownBy(() -> transmitSlackMessageFacade.transmit(command))
                .isInstanceOf(ApiException.class);

        then(slackClient).should(never()).transmitSlackMessage(any());
        then(transmitSlackMessageEventProducer).should(never()).produce(any(), any());
    }
}
