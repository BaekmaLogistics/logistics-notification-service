package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserFeignResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${user.service.url:}", configuration = OpenFeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{userId}")
    GeneralResponse<UserFeignResponse> getUserById(@PathVariable UUID userId);
}
