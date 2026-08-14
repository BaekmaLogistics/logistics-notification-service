package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.ProductServiceClient;
import com.sparta.logistics.notification.application.command.model.ProductInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.CompanyFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.ProductResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class ProductServiceClientImpl implements ProductServiceClient {

    private final CompanyFeignClient productFeignClient;

    @Override
    public ProductInfo getProductInfo(UUID uuid) {
        GeneralResponse<ProductResponse> response = productFeignClient.getProductById(uuid);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "상품 정보를 찾을 수 없습니다.");
        }

        return response.data().toInfo();
    }
}
