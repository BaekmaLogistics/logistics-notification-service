package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.model.DeliveryInfo;
import com.sparta.logistics.notification.application.command.model.DeliveryRouteInfo;

import java.util.List;
import java.util.UUID;

public interface DeliveryServiceClient {
    DeliveryInfo getDeliveryInfo(UUID uuid);
    List<DeliveryRouteInfo> getDeliveryRoutes(UUID deliveryId);
}
