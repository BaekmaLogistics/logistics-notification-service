package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.UserServiceClient;
import com.sparta.logistics.notification.application.command.model.UserInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.UserFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.SearchUsersRequest;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class UserServiceClientImpl implements UserServiceClient {
    // TODO: Redis Caffeine 활용한 캐시 적용

    private final UserFeignClient userFeignClient;

    @Override
    public Map<UUID, UserInfo> searchUserSlackInfos(List<UUID> userIds) {
        GeneralResponse<List<UserResponse>> response =
                userFeignClient.searchUsersById(
                        new SearchUsersRequest(userIds)
                );

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }

        Map<UUID, UserInfo> userInfos = response.data().stream()
                .collect(Collectors.toMap(
                        UserResponse::id,
                        UserResponse::toInfo
                ));

        if (!userInfos.keySet().containsAll(userIds)) {
            log.error("userIds = {}, userInfos = {}", userIds, userInfos);
            throw new ApiException(
                    ErrorResponseCode.FEIGN_CLIENT_ERROR,
                    "일부 사용자 정보를 찾을 수 없습니다."
            );
        }

        return userInfos;
    }
}
