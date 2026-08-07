package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.dto.SlackFeignRequest;
import com.sparta.logistics.notification.infrastructure.feign.dto.SlackFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "slackFeignClient", url = "${slack.api.url}")
public interface SlackFeignClient {

    @PostMapping("/chat.postMessage")
    SlackFeignResponse sendSlackMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody SlackFeignRequest request
    );
}
