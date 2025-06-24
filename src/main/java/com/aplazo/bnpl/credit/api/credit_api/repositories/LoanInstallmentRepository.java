package com.aplazo.bnpl.credit.api.credit_api.repositories;

import org.springframework.data.repository.CrudRepository;

import com.aplazo.bnpl.credit.api.credit_api.entities.LoanInstallment;

public interface LoanInstallmentRepository extends CrudRepository<LoanInstallment, Integer> {

}
