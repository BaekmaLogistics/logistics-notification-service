package com.sparta.logistics.notification.infrastructure.external.client;

import com.sparta.logistics.notification.application.command.client.CompanyServiceClient;
import com.sparta.logistics.notification.application.command.model.CompanyInfo;
import com.sparta.logistics.notification.common.code.ErrorResponseCode;
import com.sparta.logistics.notification.common.exception.ApiException;
import com.sparta.logistics.notification.infrastructure.feign.client.CompanyFeignClient;
import com.sparta.logistics.notification.infrastructure.feign.dto.CompanyResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class CompanyServiceClientImpl implements CompanyServiceClient {

    private final CompanyFeignClient companyFeignClient;

    @Override
    public CompanyInfo getCompanyInfo(UUID uuid) {
        GeneralResponse<CompanyResponse> response = companyFeignClient.getCompanyById(uuid);

        if (response == null || response.data() == null) {
            throw new ApiException(ErrorResponseCode.FEIGN_CLIENT_ERROR, "업체 정보를 찾을 수 없습니다.");
        }

        return response.data().toInfo();
    }
}
