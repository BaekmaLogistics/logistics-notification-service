package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.dto.UpdateSlackMessageCommand;
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

    @Override
    public void transmit(TransmitSlackMessageCommand command) {
        // API Controller 호출과 시스템 호출 둘 다 받아야 함..

        try {
            SlackMessage slackMessage = slackMessageCommandService.getSlackMessage(command.slackMessageId());

            // 외부 I/O (Slack API 호출) - DB 트랜잭션 없이 백그라운드 수행
            slackClient.sendSlackMessage(slackMessage);

            SlackMessage completed = slackMessage.complete();

            slackMessageCommandService.update(
                    completed.id(),
                    new UpdateSlackMessageCommand(
                            completed.content()
                    ),
                    command.actorId()
            );
        } catch (Exception e) {
            log.error("Failed to transmit Slack message: {}", e.getMessage(), e);
            // TODO: 실패 시 DB 상태 업데이트 (slackMessageCommandService내 @Transactional 적용 메서드 호출)
            throw e;
        }
    }
}
