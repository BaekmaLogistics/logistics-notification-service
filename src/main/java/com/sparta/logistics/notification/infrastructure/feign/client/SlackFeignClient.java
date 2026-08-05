package com.sparta.logistics.notification.infrastructure.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "slackFeignClient", url = "${slack.api.url}")
public interface SlackFeignClient {
}
