package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.dto.UserInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.UserFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserFeignResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class UserServiceClientImpl implements UserServiceClient {
    // TODO: Redis Caffeine 활용한 캐시 적용

    private final UserFeignClient userFeignClient;

    @Override
    public Map<UUID, UserInfo> searchUserSlackInfos(List<UUID> userIds) {
        GeneralResponse<Map<UUID, UserFeignResponse>> response =
                userFeignClient.searchUsersById(userIds);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }

        HashMap<UUID, UserInfo> resultMap = new HashMap<>();
        response.data().forEach((userId, feignResponse) ->
                resultMap.put(userId, feignResponse.toInfo(userId))
        );

        return resultMap;
    }
}
