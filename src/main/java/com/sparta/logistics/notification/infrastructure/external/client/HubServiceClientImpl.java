package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.HubServiceClient;
import com.sparta.logistics.notification.application.command.model.HubInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.HubFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.HubResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class HubServiceClientImpl implements HubServiceClient {

    private final HubFeignClient hubFeignClient;

    @Override
    public HubInfo getHubInfo(UUID hubId) {
        GeneralResponse<HubResponse> response = hubFeignClient.getHubById(hubId);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "허브 정보를 찾을 수 없습니다.");
        }

        return response.data().toInfo();
    }

    @Override
    public Map<UUID, HubInfo> getHubInfos(List<UUID> hubIds) {
        GeneralResponse<List<HubResponse>> response = hubFeignClient.getHubsByIds(hubIds);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "허브 정보를 찾을 수 없습니다.");
        }

        Map<UUID, HubInfo> hubInfos = response.data().stream()
                .collect(Collectors.toMap(
                        HubResponse::id,
                        HubResponse::toInfo
                ));

        if (!hubInfos.keySet().containsAll(hubIds)) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "일부 허브 정보를 찾을 수 없습니다.");
        }

        return hubInfos;
    }
}
