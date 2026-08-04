package com.sparta.notification.presentation.common.dto.response;

import java.util.Map;

public record FeignErrorResponse(
        String errorCode,
        String message,
        Map<String, String> errors
) {
}
