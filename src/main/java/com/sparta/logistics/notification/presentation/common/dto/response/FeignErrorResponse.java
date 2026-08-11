package com.sparta.logistics.notification.presentation.common.dto.response;

import java.util.Map;

public record FeignErrorResponse(
        String errorCode,
        String message,
        Map<String, String> errors
) {
}
