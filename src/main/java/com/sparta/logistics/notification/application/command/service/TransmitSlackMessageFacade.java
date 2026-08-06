package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.producer.TransmitSlackMessageEventProducer;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class TransmitSlackMessageFacade implements TransmitSlackMessageUseCase {
    private final SlackMessageCommandService slackMessageCommandService;
    private final SlackClient slackClient;
    private final TransmitSlackMessageEventProducer transmitSlackMessageEventProducer;

    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public void transmit(TransmitSlackMessageCommand command) {
        // PENDING 또는 RETRYING -> PROCESSING (멱등성 락 획득)
        SlackMessage slackMessage = slackMessageCommandService.updateStatusToProcessing(
                command.slackMessageId(), command.actorId()
        );

        try {
            // 외부 API 호출
            slackClient.transmitSlackMessage(slackMessage);

            // 성공 시 PROCESSING -> SUCCESS 갱신
            slackMessageCommandService.updateStatusToSuccess(command.slackMessageId(), command.actorId());

        } catch (Exception e) {
            log.warn("Failed to transmit Slack message: id={}, retryCount={}, error={}",
                    command.slackMessageId(), slackMessage.retryCount(), e.getMessage());

            if (slackMessage.retryCount() < MAX_RETRY_COUNT) {
                // PROCESSING -> RETRYING 갱신 (retryCount + 1)
                slackMessageCommandService.updateStatusToRetrying(command.slackMessageId(), command.actorId());

                // 이벤트 재발급
                transmitSlackMessageEventProducer.produce(
                        slackMessage.id(),
                        command.actorId()
                );
            } else {
                // PROCESSING -> FAILED 갱신
                slackMessageCommandService.updateStatusToFailed(command.slackMessageId(), e.getMessage(), command.actorId());
            }

            throw e;
        }
    }
}
