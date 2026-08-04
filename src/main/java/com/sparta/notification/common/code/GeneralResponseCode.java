package com.sparta.notification.common.code;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GeneralResponseCode implements ApiResponseCode {
    // Common
    OK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "성공적으로 생성되었습니다."),
    ACCEPTED(HttpStatus.ACCEPTED, "요청이 성공적으로 접수되었습니다.");

    private final HttpStatus status;
    private final String message;
}