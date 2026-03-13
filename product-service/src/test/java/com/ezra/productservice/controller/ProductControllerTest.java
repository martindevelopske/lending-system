package com.ezra.productservice.controller;

import com.ezra.productservice.dtos.*;
import com.ezra.productservice.enums.CalcMethod;
import com.ezra.productservice.enums.FeeType;
import com.ezra.productservice.enums.LoanStructure;
import com.ezra.productservice.enums.TenureType;
import com.ezra.productservice.exception.FeeNotFoundException;
import com.ezra.productservice.exception.ProductNotFoundException;
import com.ezra.productservice.service.FeeService;
import com.ezra.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private FeeService feeService;

    private ProductDto sampleProduct() {
        return ProductDto.builder()
                .id(UUID.randomUUID())
                .name("Nano Loan")
                .description("Small short-term loan")
                .tenureType(TenureType.DAYS)
                .tenureValue(new BigDecimal("30"))
                .loanStructure(LoanStructure.LUMPSUM)
                .interestRate(5)
                .minimumAmount(new BigDecimal("100"))
                .maximumAmount(new BigDecimal("5000"))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .fees(List.of())
                .build();
    }

    // --- GET /api/v1/products ---

    @Test
    void getAllProducts_returnsListOfProducts() throws Exception {
        List<ProductDto> products = List.of(sampleProduct(), sampleProduct());
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Nano Loan"));
    }

    @Test
    void getAllProducts_emptyList_returnsOk() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/v1/products/{id} ---

    @Test
    void getProductById_existingProduct_returnsProduct() throws Exception {
        UUID id = UUID.randomUUID();
        ProductDto product = sampleProduct();
        product.setId(id);
        when(productService.getProductById(id)).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nano Loan"))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getProductById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductById(id)).thenThrow(new ProductNotFoundException("Product not found: " + id));

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found: " + id));
    }

    // --- POST /api/v1/products ---

    @Test
    void createProduct_validRequest_returns201() throws Exception {
        ProductCreationRequest request = ProductCreationRequest.builder()
                .name("Personal Loan")
                .description("Medium term loan")
                .tenureType(TenureType.MONTHS)
                .tenureValue(new BigDecimal("6"))
                .loanStructure(LoanStructure.INSTALLMENT)
                .interestRate(12)
                .minimumAmount(new BigDecimal("1000"))
                .maximumAmount(new BigDecimal("100000"))
                .isActive(true)
                .build();

        ProductDto response = sampleProduct();
        response.setName("Personal Loan");
        when(productService.createProduct(any(ProductCreationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Personal Loan"));
    }

    // --- DELETE /api/v1/products/{id} ---

    @Test
    void deleteProduct_existingProduct_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(id);
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ProductNotFoundException("Product not found")).when(productService).deleteProduct(id);

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/v1/products/{productId}/fees ---

    @Test
    void addFee_validRequest_returns201() throws Exception {
        UUID productId = UUID.randomUUID();
        FeeCreationRequest request = FeeCreationRequest.builder()
                .name("Processing Fee")
                .feeType(FeeType.SERVICE)
                .calculationMethod(CalcMethod.PERCENTAGE)
                .amount(new BigDecimal("2.5"))
                .build();

        FeeDto feeResponse = FeeDto.builder()
                .id(UUID.randomUUID())
                .name("Processing Fee")
                .feeType(FeeType.SERVICE)
                .calculationMethod(CalcMethod.PERCENTAGE)
                .amount(new BigDecimal("2.5"))
                .productId(productId)
                .build();
        when(feeService.addFee(eq(productId), any(FeeCreationRequest.class))).thenReturn(feeResponse);

        mockMvc.perform(post("/api/v1/products/{productId}/fees", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Processing Fee"))
                .andExpect(jsonPath("$.feeType").value("SERVICE"));
    }

    // --- PUT /api/v1/products/{productId}/fees/{feeId} ---

    @Test
    void updateFee_validRequest_returns200() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID feeId = UUID.randomUUID();
        FeeCreationRequest request = FeeCreationRequest.builder()
                .name("Updated Fee")
                .feeType(FeeType.DAILY)
                .calculationMethod(CalcMethod.FIXED)
                .amount(new BigDecimal("50"))
                .build();

        FeeDto feeResponse = FeeDto.builder()
                .id(feeId)
                .name("Updated Fee")
                .feeType(FeeType.DAILY)
                .calculationMethod(CalcMethod.FIXED)
                .amount(new BigDecimal("50"))
                .productId(productId)
                .build();
        when(feeService.updateFee(eq(productId), eq(feeId), any(FeeCreationRequest.class))).thenReturn(feeResponse);

        mockMvc.perform(put("/api/v1/products/{productId}/fees/{feeId}", productId, feeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Fee"));
    }

    @Test
    void updateFee_feeNotFound_returns404() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID feeId = UUID.randomUUID();
        FeeCreationRequest request = FeeCreationRequest.builder()
                .name("Fee")
                .feeType(FeeType.SERVICE)
                .calculationMethod(CalcMethod.FIXED)
                .amount(new BigDecimal("100"))
                .build();

        when(feeService.updateFee(eq(productId), eq(feeId), any(FeeCreationRequest.class)))
                .thenThrow(new FeeNotFoundException("Fee not found"));

        mockMvc.perform(put("/api/v1/products/{productId}/fees/{feeId}", productId, feeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/v1/products/{productId}/fees/{feeId} ---

    @Test
    void removeFee_existingFee_returns204() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID feeId = UUID.randomUUID();
        doNothing().when(feeService).removeFee(productId, feeId);

        mockMvc.perform(delete("/api/v1/products/{productId}/fees/{feeId}", productId, feeId))
                .andExpect(status().isNoContent());

        verify(feeService).removeFee(productId, feeId);
    }

    @Test
    void removeFee_notFound_returns404() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID feeId = UUID.randomUUID();
        doThrow(new FeeNotFoundException("Fee not found")).when(feeService).removeFee(productId, feeId);

        mockMvc.perform(delete("/api/v1/products/{productId}/fees/{feeId}", productId, feeId))
                .andExpect(status().isNotFound());
    }
}
