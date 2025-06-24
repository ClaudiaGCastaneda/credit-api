package com.aplazo.bnpl.credit.api.credit_api.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplazo.bnpl.credit.api.credit_api.dto.LoanRequestDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.entities.Loan;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;
import com.aplazo.bnpl.credit.api.credit_api.entities.PurchaseStatus;
import com.aplazo.bnpl.credit.api.credit_api.exception.PurchaseException;
import com.aplazo.bnpl.credit.api.credit_api.repositories.LoanRepository;
import com.aplazo.bnpl.credit.api.credit_api.util.CommissionCalculator;

@Service
public class LoanServiceImpl implements LoanService {
        private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

        @Autowired
        private CustomerService customerService;
        @Autowired
        private PaymentSchemaService paymentSchemaService;
        @Autowired
        private LoanInstallmentService purchaseInstallmentService;
        @Autowired
        private LoanRepository repository;

        @Transactional
        @Override
        public Loan save(LoanRequestDTO loanDTO) {
                int currencyScale = 2;
                RoundingMode currencyRoundingMode = RoundingMode.HALF_UP;
                Loan loan = new Loan();
                loan.setIdClient(loanDTO.getCustomerId());

                Customer customerDb = customerService.findById(loanDTO.getCustomerId())
                                .orElseThrow(() -> {
                                        logger.error("Cliente con ID {} no encontrado para la compra.",
                                                        loanDTO.getCustomerId());
                                        return new PurchaseException("Cliente con ID " + loanDTO.getCustomerId()
                                                        + " no encontrado.");
                                });

                System.out.println(customerDb.getName());
                loan.setPurchaseDate(LocalDateTime.now());
                loan.setCreditAvailableBefore(customerDb.getAvailable_credit());

                BigDecimal availableCredit = customerDb.getAvailable_credit();
                BigDecimal purchaseAmount = loanDTO.getAmount();
                loan.setAmount(purchaseAmount);

                if (availableCredit.compareTo(purchaseAmount) < 0) {
                        logger.info(
                                        "Crédito insuficiente para el monto inicial de la compra (ID Cliente: {}, Disponible: {}, Solicitado: {}). Compra rechazada.",
                                        customerDb.getId(), availableCredit, purchaseAmount);
                        loan.setStatus(PurchaseStatus.REJECTED);
                        repository.save(loan);
                        throw new PurchaseException("Crédito insuficiente para el monto inicial de la compra.");
                }

                logger.info("Credito suficiente para la compra");

                PaymentSchema paymentSchemaDb = paymentSchemaService.get(customerDb)
                                .orElseThrow(() -> {
                                        logger.error("No se encontró un esquema de pago para el cliente {}. La compra no puede ser procesada.",
                                                        customerDb.getId());
                                        loan.setStatus(PurchaseStatus.REJECTED);
                                        repository.save(loan);
                                        return new PurchaseException(
                                                        "No se encontró un esquema de pago aplicable para el cliente.");
                                });

                loan.setIdPaymentSchema(paymentSchemaDb.getOrderId());
                logger.info("Encontre schema para el cliente " + paymentSchemaDb.getDescription());

                BigDecimal comissionAmount = CommissionCalculator.calculateCommission(
                                loan.getAmount(),
                                paymentSchemaDb.getInterestRate());

                loan.setCommissionAmount(comissionAmount);

                BigDecimal totalAmount = loan.getAmount().add(comissionAmount);
                totalAmount = totalAmount.setScale(currencyScale, currencyRoundingMode);

                loan.setTotalAmount(totalAmount);

                loan.setStatus(PurchaseStatus.APPROVED);

                BigDecimal creditAvailableAfter = customerDb.getAvailable_credit()
                                .subtract(totalAmount);

                loan.setCreditAvailableAfter(creditAvailableAfter);

                Loan savedLoan = repository.save(loan);
                loan.setId(savedLoan.getId());

                customerDb.setAvailable_credit(creditAvailableAfter);
                customerService.update(customerDb.getId(), customerDb);

                purchaseInstallmentService.save(loan, paymentSchemaDb);

                return loan;

        }

}
