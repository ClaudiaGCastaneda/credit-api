package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;
import com.aplazo.bnpl.credit.api.credit_api.repositories.CreditLineRepository;

@Service
public class CreditLineImpl implements CreditLineService {

    @Autowired
    private CreditLineRepository repository;

    @Transactional(readOnly = true)
    @Override
    public Optional<CreditLine> get(int age) {
        Optional<CreditLine> creditLine = repository
                .findFirstByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndActiveTrue(age, age);
        return creditLine;
    }

    @Transactional
    @Override
    public void deleteAllRules() {
        repository.deleteAll();

    }

}
