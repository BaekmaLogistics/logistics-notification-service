package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserResponse;
import com.sparta.logistics.notification.infrastructure.feign.fallback.UserFeignClientFallbackFactory;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        configuration = OpenFeignConfig.class,
        fallbackFactory = UserFeignClientFallbackFactory.class // FallbackFactory 지정
)
public interface UserFeignClient {

    @PostMapping("/internal/api/v1/users/search")
    GeneralResponse<List<UserResponse>> searchUsersById(
            @RequestBody List<UUID> userIds
    );
}
