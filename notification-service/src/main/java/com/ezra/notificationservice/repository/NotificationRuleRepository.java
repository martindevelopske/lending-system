package com.ezra.notificationservice.repository;

import com.ezra.notificationservice.models.NotificationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationRuleRepository extends JpaRepository<NotificationRule, UUID> {

    @Query("SELECT r FROM NotificationRule r WHERE r.eventType = :eventType AND r.active = true ORDER BY r.priority ASC")
    List<NotificationRule> findActiveRulesForEvent(String eventType);
}
