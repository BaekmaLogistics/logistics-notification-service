package com.sparta.logistics.notification.infrastructure.feign.fallback;

import com.sparta.logistics.notification.common.code.GeneralResponseCode;
import com.sparta.logistics.notification.infrastructure.feign.client.UserFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.SearchUsersRequest;
import com.sparta.logistics.notification.infrastructure.feign.dto.UserResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public GeneralResponse<List<UserResponse>> searchUsersById(SearchUsersRequest request) {
                // Feign 호출 실패 시 원인 예외 로깅
                log.error("[UserFeignClient] searchUsersById 호출 실패 - cause: {}", cause.getMessage(), cause);

                // 빈 목록을 담은 기본 성공 응답 반환 (프로젝트 응답 구조에 맞게 조율)
                return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, Collections.<UserResponse>emptyList()).getBody();
            }
        };
    }
}