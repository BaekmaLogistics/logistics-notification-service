package com.sparta.notification.presentation.common.dto.response;

import org.springframework.http.HttpStatus;

public interface ApiResponseCode {
    HttpStatus getStatus();
    String getMessage();
}
