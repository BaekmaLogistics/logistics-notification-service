package com.sparta.logistics.notification.application.command.client;

import com.sparta.logistics.notification.application.command.dto.UserSlackInfo;

import java.util.UUID;

public interface UserServiceClient {
    UserSlackInfo getUserSlackInfo(UUID userId);
}
