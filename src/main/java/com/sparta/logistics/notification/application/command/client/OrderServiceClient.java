package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.model.OrderInfo;

import java.util.UUID;

public interface OrderServiceClient {
    OrderInfo getOrderInfo(UUID uuid);
}
