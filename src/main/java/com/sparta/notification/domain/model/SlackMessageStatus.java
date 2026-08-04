package com.sparta.notification.domain.model;

public enum SlackMessageStatus {
    PENDING,
    SUCCESS,
    FAILED,
    RETRYING;
    /*
        - PENDING -> SUCCESS
        - PENDING -> FAILED
        - FAILED -> RETRYING
        - RETRYING -> SUCCESS
        - RETRYING -> FAILED
     */

    public boolean canTransitionTo(SlackMessageStatus target) {
        return switch (this) {
            case PENDING, RETRYING -> target == SUCCESS || target == FAILED;
            case FAILED -> target == RETRYING;
            case SUCCESS -> false; // SUCCESS는 최종 상태이므로 전이 불가
        };
    }
}
