package com.aplazo.bnpl.credit.api.credit_api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.aplazo.bnpl.credit.api.credit_api.dto.LoanRequestDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.Loan;
import com.aplazo.bnpl.credit.api.credit_api.entities.PurchaseStatus;
import com.aplazo.bnpl.credit.api.credit_api.exception.PurchaseException;
import com.aplazo.bnpl.credit.api.credit_api.services.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateLoan_ReturnsCreated() throws Exception {
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("400.80");

        LoanRequestDTO request = new LoanRequestDTO();
        request.setCustomerId(customerId);
        request.setAmount(amount);

        Loan mockLoan = new Loan();
        mockLoan.setId(loanId);
        mockLoan.setIdClient(customerId);
        mockLoan.setAmount(amount);
        mockLoan.setStatus(PurchaseStatus.APPROVED);
        mockLoan.setPurchaseDate(LocalDateTime.now());

        Mockito.when(loanService.save(any())).thenReturn(mockLoan);

        mockMvc.perform(post("/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/v1/loans/" + loanId))
                .andExpect(jsonPath("$.id").value(loanId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.amount").value(amount.doubleValue()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testCreateLoan_ReturnsBadRequest_WhenPurchaseException() throws Exception {
        LoanRequestDTO request = new LoanRequestDTO();
        request.setCustomerId(UUID.randomUUID());
        request.setAmount(new BigDecimal("9999"));

        Mockito.when(loanService.save(any()))
                .thenThrow(new PurchaseException("Insufficient credit"));

        mockMvc.perform(post("/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_LOAN_REQUEST"))
                .andExpect(jsonPath("$.message").value("Insufficient credit"));
    }

    @Test
    void testCreateLoan_ReturnsInternalServerError_OnUnexpectedException() throws Exception {
        LoanRequestDTO request = new LoanRequestDTO();
        request.setCustomerId(UUID.randomUUID());
        request.setAmount(new BigDecimal("500"));

        Mockito.when(loanService.save(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
