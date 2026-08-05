package com.likelion.hackatonbe.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    IDEMPOTENCY_KEY_REQUIRED(HttpStatus.BAD_REQUEST, "Idempotency-Key 헤더가 필요합니다."),
    DURATION_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "이벤트 지속 시간이 허용 범위를 벗어났습니다."),
    EVENT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "한 배치에는 최대 500개 이벤트를 전송할 수 있습니다."),
    BACKFILL_HEADER_REQUIRED(HttpStatus.BAD_REQUEST, "72시간 이전 데이터에는 X-Backfill 헤더가 필요합니다."),
    BACKFILL_TOO_OLD(HttpStatus.UNPROCESSABLE_ENTITY, "최대 72시간 이전 데이터까지만 동기화할 수 있습니다."),
    GUARDIAN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "보호자 열람 권한이 없습니다."),
    LEGAL_CONSENT_REQUIRED(HttpStatus.UNPROCESSABLE_ENTITY, "법정대리인 동의가 필요합니다."),
    ENVIRONMENT_DATA_STALE(HttpStatus.SERVICE_UNAVAILABLE, "환경 데이터가 오래되어 분석에 사용할 수 없습니다."),
    ANALYSIS_DATA_INSUFFICIENT(HttpStatus.UNPROCESSABLE_ENTITY, "분석을 위한 데이터가 부족합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
