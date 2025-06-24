package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;
import com.aplazo.bnpl.credit.api.credit_api.repositories.PaymentSchemaRepository;

@Service
public class PaymentSchemaServiceImpl implements PaymentSchemaService {

    private static final Set<String> SCHEMA_ONE_INITIALS = new HashSet<>(Arrays.asList("C", "L", "H"));

    @Autowired
    private PaymentSchemaRepository repository;

    @Override
    public Optional<PaymentSchema> get(Customer customer) {
        // asignar schema

        String clientFirstNameInitial = customer.getName().trim().substring(0, 1).toUpperCase();
        String schemaToFind = null;

        if (SCHEMA_ONE_INITIALS.contains(clientFirstNameInitial)) {
            schemaToFind = "Schema 1";
            // } else if (client.getId() > 25) { //se omite porque los id son uuids
            // schemaToFind = "Schema 2";
        } else {
            schemaToFind = "Schema 2";
        }

        Optional<PaymentSchema> foundSchemaOptional = repository.findByDescription(schemaToFind);

        return foundSchemaOptional;
    }

    @Transactional
    @Override
    public void deleteAllRules() {
        repository.deleteAll();
    }

}
