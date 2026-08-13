package com.sparta.logistics.notification.infrastructure.feign.dto;

import java.util.List;
import java.util.UUID;

public record SearchUsersRequest(
        List<UUID> userIds
) {

}
