package com.ezra.notificationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateRendererTest {

    private TemplateRenderer templateRenderer;

    @BeforeEach
    void setUp() {
        templateRenderer = new TemplateRenderer();
    }

    @Test
    void render_replacesAllVariables() {
        String template = "Dear {{firstName}}, your loan {{loanId}} of {{amount}} has been disbursed.";
        Map<String, String> variables = Map.of(
                "firstName", "John",
                "loanId", "LOAN-001",
                "amount", "10000"
        );

        String result = templateRenderer.render(template, variables);

        assertThat(result).isEqualTo("Dear John, your loan LOAN-001 of 10000 has been disbursed.");
    }

    @Test
    void render_keepsUnknownVariables() {
        String template = "Dear {{firstName}}, balance: {{outstandingBalance}}";
        Map<String, String> variables = Map.of("firstName", "Jane");

        String result = templateRenderer.render(template, variables);

        assertThat(result).isEqualTo("Dear Jane, balance: {{outstandingBalance}}");
    }

    @Test
    void render_withNullTemplate_returnsEmptyString() {
        String result = templateRenderer.render(null, Map.of("key", "value"));

        assertThat(result).isEmpty();
    }

    @Test
    void render_withEmptyTemplate_returnsEmptyString() {
        String result = templateRenderer.render("", Map.of("key", "value"));

        assertThat(result).isEmpty();
    }

    @Test
    void render_withNullVariables_returnsOriginalTemplate() {
        String template = "Hello {{name}}";
        String result = templateRenderer.render(template, null);

        assertThat(result).isEqualTo("Hello {{name}}");
    }

    @Test
    void render_noVariablesInTemplate_returnsUnchanged() {
        String template = "This is a plain message with no variables.";
        String result = templateRenderer.render(template, Map.of("key", "value"));

        assertThat(result).isEqualTo(template);
    }
}
