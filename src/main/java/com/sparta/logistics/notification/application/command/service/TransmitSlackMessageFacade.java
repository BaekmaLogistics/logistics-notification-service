package com.sparta.logistics.notification.application.command.service;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.application.command.dto.TransmitSlackMessageCommand;
import com.sparta.logistics.notification.application.command.usecase.TransmitSlackMessageUseCase;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.domain.repository.SlackMessageCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class TransmitSlackMessageFacade implements TransmitSlackMessageUseCase {
    private final SlackMessageCommandRepository slackMessageCommandRepository;
    private final SlackClient slackClient;

    @Override
    public void transmit(TransmitSlackMessageCommand command) {
        SlackMessage slackMessage = slackMessageCommandRepository.findById(command.slackMessageId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.SLACK_MESSAGE_NOT_FOUND));

        try {
            // 외부 I/O (Slack API 호출) - DB 트랜잭션 없이 백그라운드 수행
            slackClient.sendSlackMessage(slackMessage);

            // 성공 시 도메인 상태 변경 (PENDING -> SUCCESS) 및 actorId(updatedBy) 반영
            SlackMessage completed = slackMessage.complete(command.actorId());
            slackMessageCommandRepository.update(completed);
        } catch (Exception e) {
            log.error("Failed to transmit Slack message: id={}, error={}", command.slackMessageId(), e.getMessage(), e);

            // 실패 시 도메인 상태 변경 (PENDING -> FAILED) 및 actorId(updatedBy) 반영
            SlackMessage failed = slackMessage.fail(e.getMessage(), command.actorId());
            slackMessageCommandRepository.update(failed);
            throw e;
        }
    }
}
