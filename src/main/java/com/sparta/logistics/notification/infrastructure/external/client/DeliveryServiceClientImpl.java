package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.DeliveryServiceClient;
import com.sparta.logistics.notification.application.command.model.DeliveryInfo;
import com.sparta.logistics.notification.application.command.model.DeliveryRouteInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.DeliveryFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.DeliveryResponse;
import com.sparta.logistics.notification.infrastructure.feign.dto.DeliveryRouteResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class DeliveryServiceClientImpl implements DeliveryServiceClient {

    private final DeliveryFeignClient deliveryFeignClient;

    @Override
    public DeliveryInfo getDeliveryInfo(UUID uuid) {
        GeneralResponse<DeliveryResponse> response = deliveryFeignClient.getDeliveryById(uuid);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "배송 정보를 찾을 수 없습니다.");
        }

        return response.data().toInfo();
    }

    @Override
    public List<DeliveryRouteInfo> getDeliveryRoutes(UUID deliveryId) {
        GeneralResponse<List<DeliveryRouteResponse>> response = deliveryFeignClient.getDeliveryRoutesById(deliveryId);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "배송 경로 정보를 찾을 수 없습니다.");
        }

        return response.data().stream()
                .map(DeliveryRouteResponse::toInfo)
                .toList();
    }
}
