package com.aplazo.bnpl.credit.api.credit_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_schemas")
public class PaymentSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description", nullable = false, length = 20)
    private String description;

    @Column(name = "order_id", nullable = false, length = 10)
    private Integer orderId;

    @Column(name = "payment_numbers", nullable = false)
    private Integer paymentNumbers;

    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 4) // 0.13 para 13%
    private BigDecimal interestRate;

    public PaymentSchema() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPaymentNumbers() {
        return paymentNumbers;
    }

    public void setPaymentNumbers(Integer paymentNumbers) {
        this.paymentNumbers = paymentNumbers;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "PaymentSchema{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentNumbers=" + paymentNumbers +
                ", frequency='" + frequency + '\'' +
                ", interestRate=" + interestRate +
                '}';
    }

}