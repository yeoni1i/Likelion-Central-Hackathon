package com.likelion.hackatonbe.domain.environment.schedular;

import com.likelion.hackatonbe.domain.environment.repository.EnvironmentDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnvironmentCleanupScheduler {

    private final EnvironmentDataRepository environmentDataRepository;

    // 매일 새벽 4시에 실행
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void deleteOldEnvironmentData() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        environmentDataRepository.deleteByRecordedAtBefore(thirtyDaysAgo);
        log.info("30일 이전의 환경 데이터 삭제 완료: 기준 시각 = {}", thirtyDaysAgo);
    }
}