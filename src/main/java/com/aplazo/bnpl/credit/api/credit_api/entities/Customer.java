package com.aplazo.bnpl.credit.api.credit_api.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @NotEmpty(message = "{notEmpty.client.name}")
    @Size(min = 2, max = 60)
    private String name;

    @Pattern(regexp = "^\\d{4}/(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])$", message = "{pattern.client.birthdate}")
    private String birthDate;
    private Integer credit_limit_by_id;
    private BigDecimal available_credit;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getCredit_limit_by_id() {
        return credit_limit_by_id;
    }

    public void setCredit_limit_by_id(Integer credit_limit_by_id) {
        this.credit_limit_by_id = credit_limit_by_id;
    }

    public BigDecimal getAvailable_credit() {
        return available_credit;
    }

    public void setAvailable_credit(BigDecimal available_credit) {
        this.available_credit = available_credit;
    }

}
