package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.UserSlackInfo;
import com.sparta.logistics.notification.infrastructure.feign.client.UserFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserFeignResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class UserServiceClientImpl implements UserServiceClient {
    private final UserFeignClient userFeignClient;

    @Override
    public UserSlackInfo getUserSlackInfo(UUID userId) {
        GeneralResponse<UserFeignResponse> response = userFeignClient.getUserById(userId);
        return response.data().toInfo();
    }
}
