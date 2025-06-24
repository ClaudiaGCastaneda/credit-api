package com.aplazo.bnpl.credit.api.credit_api.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;

public interface PaymentSchemaRepository extends CrudRepository<PaymentSchema, Integer> {

    Optional<PaymentSchema> findByDescription(String description);

}
