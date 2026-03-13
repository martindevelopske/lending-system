package com.ezra.loanservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI loanServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Service API")
                        .description("API for managing loans, repayments, installments, and billing")
                        .version("v1")
                        .contact(new Contact()
                                .name("Ezra")
                        )
                );
    }
}
