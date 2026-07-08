package com.ezra.notificationservice.repository;

import com.ezra.notificationservice.models.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {

    void deleteByProcessedAtBefore(LocalDateTime cutoff);
}
