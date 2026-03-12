package com.ezra.notificationservice.service;

import com.ezra.notificationservice.dto.TemplateCreateRequest;
import com.ezra.notificationservice.dto.TemplateResponse;
import com.ezra.notificationservice.mapper.TemplateMapper;
import com.ezra.notificationservice.models.NotificationTemplate;
import com.ezra.notificationservice.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final NotificationTemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Transactional
    @Override
    public TemplateResponse createTemplate(TemplateCreateRequest request) {
        NotificationTemplate template = templateMapper.toEntity(request);
        template = templateRepository.save(template);
        log.info("Created template for event {} / channel {}", request.getEventType(), request.getChannel());
        return templateMapper.toResponse(template);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TemplateResponse> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(templateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public TemplateResponse getTemplate(UUID id) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));
        return templateMapper.toResponse(template);
    }

    @Transactional
    @Override
    public void deleteTemplate(UUID id) {
        templateRepository.deleteById(id);
        log.info("Deleted template: {}", id);
    }
}
