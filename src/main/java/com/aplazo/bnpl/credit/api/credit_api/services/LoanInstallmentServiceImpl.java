package com.aplazo.bnpl.credit.api.credit_api.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.credit.api.credit_api.entities.InstallmentStatus;
import com.aplazo.bnpl.credit.api.credit_api.entities.Loan;
import com.aplazo.bnpl.credit.api.credit_api.entities.LoanInstallment;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;
import com.aplazo.bnpl.credit.api.credit_api.repositories.LoanInstallmentRepository;

@Service
public class LoanInstallmentServiceImpl implements LoanInstallmentService {
    private static final Logger logger = LoggerFactory.getLogger(LoanInstallmentServiceImpl.class);

    @Autowired
    private LoanInstallmentRepository repository;

    @Transactional
    @Override
    public void save(Loan loan, PaymentSchema paymentSchema) {

        BigDecimal totalAmount = loan.getTotalAmount(); // This is the total amount (purchase + commission)
        Integer numberOfPayments = paymentSchema.getPaymentNumbers();
        String frequency = paymentSchema.getFrequency();

        BigDecimal amountPerInstallment = totalAmount.divide(
                new BigDecimal(numberOfPayments),
                10, // High scale for intermediate division
                RoundingMode.HALF_UP // Use HALF_UP for division
        );

        int currencyScale = 2;
        RoundingMode currencyRoundingMode = RoundingMode.HALF_UP;
        amountPerInstallment = amountPerInstallment.setScale(currencyScale, currencyRoundingMode);

        LocalDate currentDueDate = loan.getPurchaseDate().toLocalDate();

        for (int i = 0; i < numberOfPayments; i++) {

            LoanInstallment installment = new LoanInstallment();

            installment.setLoan(loan);
            installment.setStatus(InstallmentStatus.PENDING);

            currentDueDate = calculateNextDueDate(currentDueDate, frequency);
            installment.setDueDate(currentDueDate);
            installment.setAmount(amountPerInstallment);
            installment.setOutstandingAmount(amountPerInstallment);

            repository.save(installment);

            logger.info("Saved installment {} for Purchase ID {}: Amount={}, DueDate={}",
                    i + 1, loan.getId(), installment.getAmount(), installment.getDueDate());

        }

        logger.info("Successfully generated and saved {} installments for Purchase ID: {}", numberOfPayments,
                loan.getId());
    }

    private LocalDate calculateNextDueDate(LocalDate startingDate, String frequency) {

        switch (frequency.toUpperCase()) {
            case "BIWEEKLY": // Every two weeks
                return startingDate.plus(14, ChronoUnit.DAYS);
            case "MONTHLY":
                return startingDate.plusMonths(1);
            default:
                logger.warn("Unknown frequency '{}'. Defaulting to 1 month for due date calculation.", frequency);
                return startingDate.plus(14, ChronoUnit.DAYS); // Default BIWEEKLY
        }
    }
}
