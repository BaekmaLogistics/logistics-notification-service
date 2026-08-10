package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.HubResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hub-service", configuration = OpenFeignConfig.class)
public interface HubFeignClient {

    @GetMapping("/internal/api/v1/hubs/{hubId}")
    GeneralResponse<HubResponse> getHubById(
            @PathVariable UUID hubId
    );

    @PostMapping("/internal/api/v1/hubs/batch")
    GeneralResponse<List<HubResponse>> getHubsByIds(
            @RequestBody List<UUID> hubIds
    );
}
