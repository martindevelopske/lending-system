package com.ezra.notificationservice.service;

import com.ezra.notificationservice.dto.TemplateCreateRequest;
import com.ezra.notificationservice.dto.TemplateResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TemplateService {
    @Transactional
    TemplateResponse createTemplate(TemplateCreateRequest request);

    @Transactional(readOnly = true)
    List<TemplateResponse> getAllTemplates();

    @Transactional(readOnly = true)
    TemplateResponse getTemplate(UUID id);

    @Transactional
    void deleteTemplate(UUID id);
}
