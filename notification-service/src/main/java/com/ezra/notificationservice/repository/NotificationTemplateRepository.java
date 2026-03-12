package com.ezra.notificationservice.repository;

import com.ezra.notificationservice.enums.ChannelType;
import com.ezra.notificationservice.models.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {
    @Query("SELECT t FROM NotificationTemplate t WHERE t.eventType = :eventType AND t.channel = :channel AND t.isActive = true AND (t.productId = :productId OR t.productId IS NULL) ORDER BY t.productId DESC NULLS LAST")
    List<NotificationTemplate> findTemplatesForEvent(String eventType, ChannelType channel, UUID productId);
}
