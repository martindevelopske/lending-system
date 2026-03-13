package com.ezra.notificationservice.controller;

import com.ezra.notificationservice.dto.*;
import com.ezra.notificationservice.enums.ChannelType;
import com.ezra.notificationservice.enums.NotificationStatus;
import com.ezra.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    // --- POST /api/v1/notification/rules ---

    @Test
    void createRule_validRequest_returns201() throws Exception {
        RuleCreateRequest request = RuleCreateRequest.builder()
                .eventType("LOAN_DISBURSED")
                .channel(ChannelType.EMAIL)
                .priority(1)
                .build();

        RuleResponse response = RuleResponse.builder()
                .id(UUID.randomUUID())
                .eventType("LOAN_DISBURSED")
                .channel(ChannelType.EMAIL)
                .priority(1)
                .active(true)
                .build();
        when(notificationService.createRule(any(RuleCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notification/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventType").value("LOAN_DISBURSED"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.priority").value(1))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createRule_missingEventType_returns400() throws Exception {
        RuleCreateRequest request = RuleCreateRequest.builder()
                .channel(ChannelType.EMAIL)
                .priority(1)
                .build();

        mockMvc.perform(post("/api/v1/notification/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.eventType").exists());
    }

    @Test
    void createRule_missingChannel_returns400() throws Exception {
        RuleCreateRequest request = RuleCreateRequest.builder()
                .eventType("LOAN_DISBURSED")
                .priority(1)
                .build();

        mockMvc.perform(post("/api/v1/notification/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/v1/notification/rules ---

    @Test
    void getAllRules_returnsList() throws Exception {
        List<RuleResponse> rules = List.of(
                RuleResponse.builder()
                        .id(UUID.randomUUID())
                        .eventType("LOAN_DISBURSED")
                        .channel(ChannelType.EMAIL)
                        .priority(1)
                        .active(true)
                        .build(),
                RuleResponse.builder()
                        .id(UUID.randomUUID())
                        .eventType("LOAN_DISBURSED")
                        .channel(ChannelType.SMS)
                        .priority(2)
                        .active(true)
                        .build()
        );
        when(notificationService.getAllRules()).thenReturn(rules);

        mockMvc.perform(get("/api/v1/notification/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].channel").value("EMAIL"))
                .andExpect(jsonPath("$[1].channel").value("SMS"));
    }

    @Test
    void getAllRules_emptyList_returnsOk() throws Exception {
        when(notificationService.getAllRules()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notification/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/v1/notification/customer/{customerId} ---

    @Test
    void getCustomerNotifications_returnsList() throws Exception {
        UUID customerId = UUID.randomUUID();
        List<NotificationLogResponse> logs = List.of(
                NotificationLogResponse.builder()
                        .id(UUID.randomUUID())
                        .customerId(customerId)
                        .loanId(UUID.randomUUID())
                        .channel(ChannelType.EMAIL)
                        .eventType("LOAN_DISBURSED")
                        .subject("Loan Disbursed Successfully")
                        .content("Dear John, your loan has been disbursed.")
                        .status(NotificationStatus.SENT)
                        .retryCount(0)
                        .sentAt(LocalDateTime.now())
                        .build()
        );
        when(notificationService.getCustomerNotifications(customerId)).thenReturn(logs);

        mockMvc.perform(get("/api/v1/notification/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].eventType").value("LOAN_DISBURSED"))
                .andExpect(jsonPath("$[0].status").value("SENT"));
    }

    @Test
    void getCustomerNotifications_noLogs_returnsEmptyList() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(notificationService.getCustomerNotifications(customerId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notification/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/v1/notification/loan/{loanId} ---

    @Test
    void getLoanNotifications_returnsList() throws Exception {
        UUID loanId = UUID.randomUUID();
        List<NotificationLogResponse> logs = List.of(
                NotificationLogResponse.builder()
                        .id(UUID.randomUUID())
                        .customerId(UUID.randomUUID())
                        .loanId(loanId)
                        .channel(ChannelType.SMS)
                        .eventType("LOAN_OVERDUE")
                        .subject("Loan Overdue")
                        .content("Your loan is overdue.")
                        .status(NotificationStatus.SENT)
                        .retryCount(0)
                        .sentAt(LocalDateTime.now())
                        .build()
        );
        when(notificationService.getLoanNotifications(loanId)).thenReturn(logs);

        mockMvc.perform(get("/api/v1/notification/loan/{loanId}", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].channel").value("SMS"))
                .andExpect(jsonPath("$[0].eventType").value("LOAN_OVERDUE"));
    }
}
