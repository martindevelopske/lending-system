package com.ezra.notificationservice.event;

import com.ezra.notificationservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Scheduled job that cleans up old processed event records.
 * Events older than 7 days (matching Kafka's default retention period)
 * can never be replayed, so their deduplication entries are no longer needed.
 * Runs daily at 4 AM to prevent the processed_events table from growing indefinitely.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessedEventCleanupJob {

    private final ProcessedEventRepository processedEventRepository;

    @Scheduled(cron = "0 0 4 * * *") // 4 AM daily
    @Transactional
    public void cleanupOldProcessedEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        log.info("Cleaning up processed events older than {}", cutoff);
        processedEventRepository.deleteByProcessedAtBefore(cutoff);
        log.info("Processed event cleanup completed");
    }
}
