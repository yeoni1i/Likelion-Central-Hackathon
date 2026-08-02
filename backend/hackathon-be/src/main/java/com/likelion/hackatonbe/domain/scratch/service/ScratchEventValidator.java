package com.likelion.hackatonbe.domain.scratch.service;

import com.likelion.hackatonbe.domain.scratch.dto.ScratchEventRequest;
import com.likelion.hackatonbe.global.error.BusinessException;
import com.likelion.hackatonbe.global.error.ErrorCode;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class ScratchEventValidator {

    private static final BigDecimal ALLOWED_DURATION_DRIFT_SECONDS = new BigDecimal("1.0");

    public void validate(ScratchEventRequest event) {
        if (!event.startTs().isBefore(event.endTs())) {
            throw new BusinessException(ErrorCode.DURATION_OUT_OF_RANGE);
        }

        BigDecimal actualSeconds = BigDecimal.valueOf(
                Duration.between(event.startTs(), event.endTs()).toMillis(),
                3
        );
        if (actualSeconds.subtract(event.durationSec()).abs()
                .compareTo(ALLOWED_DURATION_DRIFT_SECONDS) > 0) {
            throw new BusinessException(ErrorCode.DURATION_OUT_OF_RANGE);
        }
    }
}
