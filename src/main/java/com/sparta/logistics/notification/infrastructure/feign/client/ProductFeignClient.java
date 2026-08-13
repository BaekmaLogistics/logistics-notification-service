package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.ProductResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-product-service", configuration = OpenFeignConfig.class)
public interface ProductFeignClient {

    @GetMapping("/internal/api/v1/products/{productId}")
    GeneralResponse<ProductResponse> getProductById(
            @PathVariable UUID productId
    );
}
