package com.aplazo.bnpl.credit.api.credit_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoanResponse", description = "Details of a newly created loan")
public class LoanResponseDTO {

    @Schema(description = "Unique identifier of the loan", example = "3fa85f64-5717-4562-b3fc-2c963f66afa7", type = "string", format = "uuid")
    private String id;

    @Schema(description = "The unique identifier of the customer associated with the loan", example = "b2863d62-0746-4b26-a6e3-edcb4b9578f2", type = "string", format = "uuid")
    private String customerId;

    @Schema(description = "The total amount of the loan", example = "400.80")
    private BigDecimal amount;

    @Schema(description = "The current status of the loan", example = "APPROVED")
    private String status;

    @Schema(description = "The date and time when the loan was created", example = "2024-06-19T10:30:00")
    private LocalDateTime createdAt;

    public LoanResponseDTO(String id, String customerId, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}