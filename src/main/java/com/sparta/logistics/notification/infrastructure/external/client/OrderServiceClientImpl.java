package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.OrderServiceClient;
import com.sparta.logistics.notification.application.command.model.OrderInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.OrderFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.OrderResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OrderServiceClientImpl implements OrderServiceClient {

    private final OrderFeignClient orderFeignClient;

    @Override
    public OrderInfo getOrderInfo(UUID uuid) {
        GeneralResponse<OrderResponse> response = orderFeignClient.getOrderById(uuid);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "주문 정보를 찾을 수 없습니다.");
        }

        return response.data().toInfo();
    }
}
