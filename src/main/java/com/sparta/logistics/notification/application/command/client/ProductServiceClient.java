package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.model.ProductInfo;

import java.util.UUID;

public interface ProductServiceClient {
    ProductInfo getProductInfo(UUID uuid);
}
