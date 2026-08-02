package com.likelion.hackatonbe.core.time;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}
