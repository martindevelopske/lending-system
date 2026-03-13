package com.ezra.notificationservice.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders notification templates by substituting Mustache-style {@code {{variable}}}
 * placeholders with values from a provided variable map. Unresolved placeholders
 * are left as-is in the output.
 */
@Component
public class TemplateRenderer {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    /** Replaces all {@code {{key}}} placeholders in the template with corresponding variable values. */
    public String render(String template, Map<String, String> variables) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        while( matcher.find() ) {
            String variableName = matcher.group(1);
            String replacement = variables.getOrDefault(variableName, matcher.group(0));
            matcher.appendReplacement(result,  Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
