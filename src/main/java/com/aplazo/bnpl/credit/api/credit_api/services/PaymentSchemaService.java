package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.Optional;

import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;

public interface PaymentSchemaService {

    Optional<PaymentSchema> get(Customer customer);

    void deleteAllRules();

}
