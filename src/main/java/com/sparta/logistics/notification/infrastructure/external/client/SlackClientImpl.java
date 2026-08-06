package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.infrastructure.feign.client.SlackFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.SlackFeignRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class SlackClientImpl implements SlackClient {
    private final SlackFeignClient slackFeignClient;

    @Override
    public void sendSlackMessage(SlackMessage slackMessage) {
        log.info("Sending message to Slack API: id={}", slackMessage.id());
        slackFeignClient.sendSlackMessage(new SlackFeignRequest(slackMessage.content()));
    }
}
