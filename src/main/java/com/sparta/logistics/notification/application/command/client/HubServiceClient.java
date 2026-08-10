package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.model.HubInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface HubServiceClient {
    HubInfo getHubInfo(UUID hubId);
    Map<UUID, HubInfo> getHubInfos(List<UUID> hubIds);
}
