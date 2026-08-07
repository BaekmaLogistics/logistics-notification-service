package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.infrastructure.feign.client.SlackFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.SlackFeignRequest;
import com.sparta.logistics.notification.infrastructure.feign.dto.SlackFeignResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SlackClientImpl implements SlackClient {
    private final SlackFeignClient slackFeignClient;

    @Value("${slack.bot.token}")
    private String botToken;

    @Override
    public void transmitSlackMessage(SlackMessage slackMessage) {
        validate(slackMessage);

        log.info("Sending Slack DM message: id={}, receiverSlackId={}", slackMessage.id(), slackMessage.receiverSlackId());

        SlackFeignRequest request = new SlackFeignRequest(slackMessage.receiverSlackId(), slackMessage.content());
        SlackFeignResponse response = slackFeignClient.sendSlackMessage("Bearer " + botToken, request);

        if (response == null || !response.ok()) {
            String errorMsg = (response != null && response.error() != null) ? response.error() : "Unknown Slack API Error";
            log.error("Failed to send Slack DM: id={}, error={}", slackMessage.id(), errorMsg);
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "Slack 전송 실패: " + errorMsg);
        }

        log.info("Successfully sent Slack DM message: id={}", slackMessage.id());
    }

    private void validate(SlackMessage slackMessage) {
        if (slackMessage == null || slackMessage.receiverSlackId() == null || slackMessage.receiverSlackId().isBlank()) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST, "수신자의 유효한 Slack ID가 존재하지 않습니다.");
        }
    }
}
