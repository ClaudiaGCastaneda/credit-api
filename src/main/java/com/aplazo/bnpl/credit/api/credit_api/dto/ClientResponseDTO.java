package com.aplazo.bnpl.credit.api.credit_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CustomerResponse", description = "Details of a newly created customer")
public class ClientResponseDTO {

    private UUID id;
    private BigDecimal assignedCreditAmount;

    public ClientResponseDTO(UUID id, BigDecimal assignedCreditAmount) {
        this.id = id;
        this.assignedCreditAmount = assignedCreditAmount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAssignedCreditAmount() {
        return assignedCreditAmount;
    }

    public void setAssignedCreditAmount(BigDecimal assignedCreditAmount) {
        this.assignedCreditAmount = assignedCreditAmount;
    }

}
