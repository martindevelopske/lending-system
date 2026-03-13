package com.ezra.notificationservice.controller;

import com.ezra.notificationservice.dto.NotificationLogResponse;
import com.ezra.notificationservice.dto.RuleCreateRequest;
import com.ezra.notificationservice.dto.RuleResponse;
import com.ezra.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for notification rule management and notification log retrieval.
 * Supports creating routing rules that map loan events to channels, and querying
 * notification history by customer or loan.
 */
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/rules")
    public ResponseEntity<RuleResponse> createRule(@Valid @RequestBody RuleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createRule(request));
    }

    @GetMapping("/rules")
    public ResponseEntity<List<RuleResponse>> getAllRules() {
        return ResponseEntity.ok(notificationService.getAllRules());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NotificationLogResponse>> getCustomerNotifications(@PathVariable UUID customerId) {
        return ResponseEntity.ok(notificationService.getCustomerNotifications(customerId));
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<NotificationLogResponse>> getLoanNotifications(@PathVariable UUID loanId) {
        return ResponseEntity.ok(notificationService.getLoanNotifications(loanId));
    }
}
