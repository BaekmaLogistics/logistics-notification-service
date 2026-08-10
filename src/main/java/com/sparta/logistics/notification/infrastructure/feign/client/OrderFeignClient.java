package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.OrderResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service", configuration = OpenFeignConfig.class)
public interface OrderFeignClient {

    @GetMapping("/internal/api/v1/orders/{orderId}")
    GeneralResponse<OrderResponse> getOrderById(
            @PathVariable UUID orderId
    );
}
