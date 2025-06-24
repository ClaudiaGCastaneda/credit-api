package com.aplazo.bnpl.credit.api.credit_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(name = "LoanRequest", description = "Details required to create a new loan")
public class LoanRequestDTO {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "The unique identifier of the customer for whom the loan is requested", example = "b2863d62-0746-4b26-a6e3-edcb4b9578f2", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "uuid")
    private UUID customerId;

    @NotNull(message = "Amount is required")
    @Schema(description = "The total amount of the loan", example = "400.80", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
