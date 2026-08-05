package com.likelion.hackatonbe.global.error;

import java.util.Map;

public record ApiErrorResponse(ErrorBody error) {

    public static ApiErrorResponse of(ErrorCode code, Map<String, ?> details) {
        return new ApiErrorResponse(new ErrorBody(code.name(), code.message(), details));
    }

    public record ErrorBody(String code, String message, Map<String, ?> details) {
    }
}
