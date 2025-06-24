package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.Optional;

import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;

public interface CreditLineService {

    Optional<CreditLine> get(int age);

    void deleteAllRules();

}
