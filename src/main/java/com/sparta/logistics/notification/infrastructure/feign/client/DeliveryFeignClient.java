package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.DeliveryResponse;
import com.sparta.logistics.notification.infrastructure.feign.dto.DeliveryRouteResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "delivery-service", configuration = OpenFeignConfig.class)
public interface DeliveryFeignClient {

    @GetMapping("/internal/api/v1/deliveries/{deliveryId}")
    GeneralResponse<DeliveryResponse> getDeliveryById(
            @PathVariable UUID deliveryId
    );

    @GetMapping("/internal/api/v1/deliveries/{deliveryId}/routes")
    GeneralResponse<List<DeliveryRouteResponse>> getDeliveryRoutesById(
            @PathVariable UUID deliveryId
    );
}
