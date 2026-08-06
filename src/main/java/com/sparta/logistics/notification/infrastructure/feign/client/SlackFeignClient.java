package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.dto.SlackFeignRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slackFeignClient", url = "${slack.api.url}")
public interface SlackFeignClient {

    @PostMapping
    void sendSlackMessage(@RequestBody SlackFeignRequest request);
}
