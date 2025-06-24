package com.aplazo.bnpl.credit.api.credit_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "id_client", nullable = false, columnDefinition = "UUID")
    private UUID idClient;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "id_payment_schema", nullable = true)
    private Integer idPaymentSchema;

    @Column(name = "commission_amount", nullable = true, precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "credit_available_before", nullable = true, precision = 10, scale = 2)
    private BigDecimal creditAvailableBefore;

    @Column(name = "credit_available_after", nullable = true, precision = 10, scale = 2)
    private BigDecimal creditAvailableAfter;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PurchaseStatus status;

    public Loan() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdClient() {
        return idClient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public UUID getIdCliente() {
        return idClient;
    }

    public void setIdClient(UUID idClient) {
        this.idClient = idClient;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getIdPaymentSchema() {
        return idPaymentSchema;
    }

    public void setIdPaymentSchema(Integer idPaymentSchema) {
        this.idPaymentSchema = idPaymentSchema;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getCreditAvailableBefore() {
        return creditAvailableBefore;
    }

    public void setCreditAvailableBefore(BigDecimal creditAvailableBefore) {
        this.creditAvailableBefore = creditAvailableBefore;
    }

    public BigDecimal getCreditAvailableAfter() {
        return creditAvailableAfter;
    }

    public void setCreditAvailableAfter(BigDecimal creditAvailableAfter) {
        this.creditAvailableAfter = creditAvailableAfter;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    // --- Método toString() para facilitar la depuración ---
    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", amount=" + amount +
                ", idClient=" + idClient +
                ", purchaseDate=" + purchaseDate +
                ", idPaymentSchema=" + idPaymentSchema +
                ", commissionAmount=" + commissionAmount +
                ", creditAvailableAfter=" + creditAvailableAfter +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}