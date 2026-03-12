package com.ezra.notificationservice.repository;

import com.ezra.notificationservice.models.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    List<NotificationLog> findByCustomerId(UUID customerId);

    List<NotificationLog> findByLoanId(UUID loanId);
}
