package com.sparta.logistics.notification.infrastructure.feign.client;

import com.sparta.logistics.notification.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.notification.infrastructure.feign.dto.CompanyResponse;
import com.sparta.logistics.notification.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", configuration = OpenFeignConfig.class)
public interface CompanyFeignClient {

    @GetMapping("/internal/api/v1/companies/{companyId}")
    GeneralResponse<CompanyResponse> getCompanyById(
            @PathVariable UUID companyId
    );
}
