package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserFeignResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service", configuration = OpenFeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/v1/users")
    GeneralResponse<Map<UUID, UserFeignResponse>> searchUsersById(
            @RequestParam("userIds") List<UUID> userIds
    );
}
