package com.ezra.notificationservice.controller;

import com.ezra.notificationservice.dto.TemplateCreateRequest;
import com.ezra.notificationservice.dto.TemplateResponse;
import com.ezra.notificationservice.enums.ChannelType;
import com.ezra.notificationservice.service.TemplateService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TemplateService templateService;

    private TemplateResponse sampleTemplate() {
        return TemplateResponse.builder()
                .id(UUID.randomUUID())
                .eventType("LOAN_DISBURSED")
                .channel(ChannelType.EMAIL)
                .subject("Loan Disbursed Successfully")
                .bodyTemplate("Dear {{firstName}}, your loan {{loanId}} has been disbursed.")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- POST /api/templates ---

    @Test
    void createTemplate_validRequest_returns201() throws Exception {
        TemplateCreateRequest request = TemplateCreateRequest.builder()
                .eventType("LOAN_DISBURSED")
                .channel(ChannelType.EMAIL)
                .subject("Loan Disbursed")
                .bodyTemplate("Dear {{firstName}}, your loan has been disbursed.")
                .build();

        TemplateResponse response = sampleTemplate();
        when(templateService.createTemplate(any(TemplateCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventType").value("LOAN_DISBURSED"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.subject").value("Loan Disbursed Successfully"));
    }

    @Test
    void createTemplate_missingEventType_returns400() throws Exception {
        TemplateCreateRequest request = TemplateCreateRequest.builder()
                .channel(ChannelType.EMAIL)
                .subject("Subject")
                .bodyTemplate("Body")
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.eventType").exists());
    }

    @Test
    void createTemplate_missingSubject_returns400() throws Exception {
        TemplateCreateRequest request = TemplateCreateRequest.builder()
                .eventType("LOAN_DISBURSED")
                .channel(ChannelType.EMAIL)
                .bodyTemplate("Body")
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTemplate_missingBody_returns400() throws Exception {
        TemplateCreateRequest request = TemplateCreateRequest.builder()
                .eventType("LOAN_DISBURSED")
                .channel(ChannelType.EMAIL)
                .subject("Subject")
                .build();

        mockMvc.perform(post("/api/v1/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/templates ---

    @Test
    void getAllTemplates_returnsList() throws Exception {
        List<TemplateResponse> templates = List.of(sampleTemplate(), sampleTemplate());
        when(templateService.getAllTemplates()).thenReturn(templates);

        mockMvc.perform(get("/api/v1/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllTemplates_emptyList_returnsOk() throws Exception {
        when(templateService.getAllTemplates()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/templates/{id} ---

    @Test
    void getTemplate_existingId_returnsTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        TemplateResponse response = sampleTemplate();
        response.setId(id);
        when(templateService.getTemplate(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/templates/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.eventType").value("LOAN_DISBURSED"));
    }

    // --- DELETE /api/templates/{id} ---

    @Test
    void deleteTemplate_existingId_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(templateService).deleteTemplate(id);

        mockMvc.perform(delete("/api/v1/templates/{id}", id))
                .andExpect(status().isNoContent());

        verify(templateService).deleteTemplate(id);
    }
}
