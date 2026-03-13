package com.ezra.customerservice.controller;

import com.ezra.customerservice.dto.*;
import com.ezra.customerservice.enums.CustomerStatus;
import com.ezra.customerservice.exception.CustomerNotFoundException;
import com.ezra.customerservice.service.CustomerService;
import com.ezra.customerservice.service.LoanLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private LoanLimitService loanLimitService;

    private CustomerResponse sampleCustomer() {
        return CustomerResponse.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Kamau")
                .email("john.kamau@email.com")
                .phoneNumber("+254712345678")
                .nationalId("ID001234")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .status(CustomerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- POST /api/v1/customers ---

    @Test
    void createCustomer_validRequest_returns201() throws Exception {
        CustomerCreateRequest request = CustomerCreateRequest.builder()
                .firstName("John")
                .lastName("Kamau")
                .email("john.kamau@email.com")
                .phoneNumber("+254712345678")
                .nationalId("ID001234")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        CustomerResponse response = sampleCustomer();
        when(customerService.createCustomer(any(CustomerCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Kamau"))
                .andExpect(jsonPath("$.email").value("john.kamau@email.com"));
    }

    @Test
    void createCustomer_missingFirstName_returns400() throws Exception {
        CustomerCreateRequest request = CustomerCreateRequest.builder()
                .lastName("Kamau")
                .email("john@email.com")
                .phoneNumber("+254712345678")
                .nationalId("ID001234")
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void createCustomer_invalidEmail_returns400() throws Exception {
        CustomerCreateRequest request = CustomerCreateRequest.builder()
                .firstName("John")
                .lastName("Kamau")
                .email("not-an-email")
                .phoneNumber("+254712345678")
                .nationalId("ID001234")
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    // --- GET /api/v1/customers/{id} ---

    @Test
    void getCustomer_existingId_returnsCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerResponse response = sampleCustomer();
        response.setId(id);
        when(customerService.getCustomer(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getCustomer_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(customerService.getCustomer(id)).thenThrow(new CustomerNotFoundException("Customer not found: " + id));

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: " + id));
    }

    // --- GET /api/v1/customers ---

    @Test
    void getAllCustomers_returnsList() throws Exception {
        List<CustomerResponse> customers = List.of(sampleCustomer(), sampleCustomer());
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // --- PUT /api/v1/customers/{id} ---

    @Test
    void updateCustomer_validRequest_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .firstName("Jane")
                .email("jane.updated@email.com")
                .build();

        CustomerResponse response = sampleCustomer();
        response.setId(id);
        response.setFirstName("Jane");
        response.setEmail("jane.updated@email.com");
        when(customerService.updateCustomer(eq(id), any(CustomerUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.updated@email.com"));
    }

    @Test
    void updateCustomer_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerUpdateRequest request = CustomerUpdateRequest.builder().firstName("Jane").build();
        when(customerService.updateCustomer(eq(id), any(CustomerUpdateRequest.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(put("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/v1/customers/{customerId}/loan-limit ---

    @Test
    void setLoanLimit_validRequest_returns200() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanLimitRequest request = LoanLimitRequest.builder()
                .maxLoanAmount(new BigDecimal("50000"))
                .build();

        LoanLimitResponse response = LoanLimitResponse.builder()
                .id(UUID.randomUUID())
                .maxLoanAmount(new BigDecimal("50000"))
                .availableAmount(new BigDecimal("50000"))
                .build();
        when(loanLimitService.setLoanLimit(eq(customerId), any(LoanLimitRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/customers/{customerId}/loan-limit", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLoanAmount").value(50000))
                .andExpect(jsonPath("$.availableAmount").value(50000));
    }

    @Test
    void setLoanLimit_nullAmount_returns400() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanLimitRequest request = new LoanLimitRequest();

        mockMvc.perform(put("/api/v1/customers/{customerId}/loan-limit", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/v1/customers/{customerId}/loan-limit ---

    @Test
    void getLoanLimit_existingCustomer_returnsLimit() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanLimitResponse response = LoanLimitResponse.builder()
                .id(UUID.randomUUID())
                .maxLoanAmount(new BigDecimal("50000"))
                .availableAmount(new BigDecimal("35000"))
                .build();
        when(loanLimitService.getLoanLimit(customerId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/{customerId}/loan-limit", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLoanAmount").value(50000))
                .andExpect(jsonPath("$.availableAmount").value(35000));
    }

    // --- POST /api/v1/customers/{customerId}/loan-limit/check ---

    @Test
    void checkLoanLimit_eligible_returnsEligible() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanLimitCheckRequest request = LoanLimitCheckRequest.builder()
                .requestedAmount(new BigDecimal("10000"))
                .build();

        LoanLimitCheckResponse response = LoanLimitCheckResponse.builder()
                .eligible(true)
                .availableAmount(new BigDecimal("50000"))
                .requestedAmount(new BigDecimal("10000"))
                .message("Customer is eligible")
                .build();
        when(loanLimitService.checkLoanLimit(eq(customerId), any(LoanLimitCheckRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers/{customerId}/loan-limit/check", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.message").value("Customer is eligible"));
    }

    @Test
    void checkLoanLimit_notEligible_returnsNotEligible() throws Exception {
        UUID customerId = UUID.randomUUID();
        LoanLimitCheckRequest request = LoanLimitCheckRequest.builder()
                .requestedAmount(new BigDecimal("100000"))
                .build();

        LoanLimitCheckResponse response = LoanLimitCheckResponse.builder()
                .eligible(false)
                .availableAmount(new BigDecimal("50000"))
                .requestedAmount(new BigDecimal("100000"))
                .message("Insufficient loan limit")
                .build();
        when(loanLimitService.checkLoanLimit(eq(customerId), any(LoanLimitCheckRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers/{customerId}/loan-limit/check", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(false));
    }
}
