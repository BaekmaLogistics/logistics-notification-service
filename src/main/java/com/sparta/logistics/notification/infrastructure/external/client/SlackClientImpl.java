package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.SlackClient;
import com.sparta.logistics.notification.domain.entity.SlackMessage;
import com.sparta.logistics.notification.infrastructure.feign.client.SlackFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SlackClientImpl implements SlackClient {
    private final SlackFeignClient slackFeignClient;

    @Override
    public void sendSlackMessage(SlackMessage slackMessage) {

    }
}
