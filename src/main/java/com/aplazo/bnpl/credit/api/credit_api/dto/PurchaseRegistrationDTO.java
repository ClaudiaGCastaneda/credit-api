package com.aplazo.bnpl.credit.api.credit_api.dto;

import java.util.UUID;

public class PurchaseRegistrationDTO {

    private UUID idCompra;

    public PurchaseRegistrationDTO(UUID idCompra) {
        this.idCompra = idCompra;
    }

    public UUID getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(UUID idCompra) {
        this.idCompra = idCompra;
    }

}
