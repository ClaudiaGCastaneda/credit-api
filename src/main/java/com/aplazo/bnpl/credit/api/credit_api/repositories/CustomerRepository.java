package com.aplazo.bnpl.credit.api.credit_api.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {

}
