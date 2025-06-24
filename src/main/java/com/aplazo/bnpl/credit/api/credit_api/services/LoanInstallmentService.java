package com.aplazo.bnpl.credit.api.credit_api.services;

import com.aplazo.bnpl.credit.api.credit_api.entities.Loan;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;

public interface LoanInstallmentService {

    void save(Loan loan, PaymentSchema paymentSchema);

}
