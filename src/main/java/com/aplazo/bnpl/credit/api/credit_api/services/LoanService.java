package com.aplazo.bnpl.credit.api.credit_api.services;

import com.aplazo.bnpl.credit.api.credit_api.dto.LoanRequestDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.Loan;

public interface LoanService {

    Loan save(LoanRequestDTO loanRequestDTO);
}
