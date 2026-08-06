package com.sparta.logistics.notification.domain.model;

public enum SlackMessageStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    RETRYING;
    /*
        - PENDING -> PROCESSING
        - PROCESSING -> SUCCESS
        - PROCESSING -> FAILED
        - PROCESSING -> RETRYING
        - RETRYING -> PROCESSING
     */

    public boolean canTransitionTo(SlackMessageStatus target) {
        return switch (this) {
            case PENDING, RETRYING -> target == PROCESSING;
            case PROCESSING -> target == SUCCESS || target == FAILED || target == RETRYING;
            case FAILED -> false;
            case SUCCESS -> false; // SUCCESS, FAILED는 최종 상태이므로 전이 불가
        };
    }
}
