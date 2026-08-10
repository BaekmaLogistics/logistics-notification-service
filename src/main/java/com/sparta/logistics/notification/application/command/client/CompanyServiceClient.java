package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.model.CompanyInfo;

import java.util.UUID;

public interface CompanyServiceClient {
    CompanyInfo getCompanyInfo(UUID uuid);
}
