package com.sparta.logistics.notification.domain.entity;

import com.sparta.logistics.notification.domain.model.AiGeneratingStatus;
import com.sparta.logistics.notification.domain.model.AuditInfo;
import com.sparta.logistics.notification.domain.model.DeletionInfo;

import java.util.UUID;

public record AiHistory(
        UUID id,
        String prompt,
        String response,
        AiGeneratingStatus status,
        int retryCount,
        String errorMessage,
        AuditInfo auditInfo,
        DeletionInfo deletionInfo
) {
}
/*
id	UUID	N	O
prompt 	VARCHAR(512)	N				LLM 호출 시 전체 프롬프트
response 	VARCHAR(128)	Y			NULL	LLM 호출 결과, NULL일 경우 호출 실패
status	VARCHAR(24)	N			PENDING	처리 상태 (PENDING, PROCESSING, SUCCESS, FAILED, RETRYING)
retry_count	INTEGER	N			0	호출 실패 시 재시도 횟수
error_message	VARCHAR(128)	Y			NULL	호출 실패 시 마지막 에러 메세지
version	BIGINT	N			0	낙관락 버전
 */
