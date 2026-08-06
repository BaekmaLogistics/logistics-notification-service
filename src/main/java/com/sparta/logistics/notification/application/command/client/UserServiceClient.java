package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.dto.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserServiceClient {
    Map<UUID, UserInfo> searchUserSlackInfos(List<UUID> userId);
}
