package com.sparta.logistics.notification.common.security;

import java.util.UUID;

public record AuthUser(
        UUID id,
        String role
) {
}
