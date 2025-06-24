package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.aplazo.bnpl.credit.api.credit_api.dto.ClientRegistrationDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.ClientResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;

public interface CustomerService {

    List<Customer> findAll();

    Optional<Customer> findById(UUID id);

    ClientResponseDTO save(ClientRegistrationDTO Customer);

    Optional<Customer> update(UUID id, Customer Customer);

    Optional<Customer> updateCredit(UUID id, Customer Customer);

    Optional<Customer> delete(Customer Customer);

}
