package com.likelion.hackatonbe.core.time;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class SystemTimeProvider implements TimeProvider {

    private final Clock clock = Clock.systemUTC();

    @Override
    public Instant now() {
        // PostgreSQL/H2 timestamp 정밀도에 맞춰 멱등 응답 재조회 시 동일한 값을 보장한다.
        return clock.instant().truncatedTo(ChronoUnit.MICROS);
    }
}
