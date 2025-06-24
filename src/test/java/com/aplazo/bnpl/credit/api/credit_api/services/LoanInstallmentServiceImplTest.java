package com.aplazo.bnpl.credit.api.credit_api.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.aplazo.bnpl.credit.api.credit_api.entities.*;
import com.aplazo.bnpl.credit.api.credit_api.repositories.LoanInstallmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class LoanInstallmentServiceImplTest {

    @InjectMocks
    private LoanInstallmentServiceImpl service;

    @Mock
    private LoanInstallmentRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_generatesCorrectInstallments_monthlyFrequency() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(UUID.randomUUID());
        loan.setTotalAmount(new BigDecimal("1000.00"));
        loan.setPurchaseDate(LocalDateTime.of(2023, 1, 15, 0, 0));

        PaymentSchema paymentSchema = new PaymentSchema();
        paymentSchema.setPaymentNumbers(4);
        paymentSchema.setFrequency("MONTHLY");

        // Act
        service.save(loan, paymentSchema);

        // Assert
        // Debe guardar 4 cuotas
        verify(repository, times(4)).save(any(LoanInstallment.class));

        // También se puede verificar la fecha y montos con un ArgumentCaptor
        ArgumentCaptor<LoanInstallment> captor = ArgumentCaptor.forClass(LoanInstallment.class);
        verify(repository, times(4)).save(captor.capture());

        LocalDate dueDateExpected = loan.getPurchaseDate().toLocalDate();
        BigDecimal expectedAmount = new BigDecimal("250.00"); // 1000/4

        for (LoanInstallment installment : captor.getAllValues()) {
            dueDateExpected = dueDateExpected.plusMonths(1);
            // Monto correcto
            assert installment.getAmount().compareTo(expectedAmount) == 0;
            // Fecha correcta
            assert installment.getDueDate().equals(dueDateExpected);
            // Estado pendiente
            assert installment.getStatus() == InstallmentStatus.PENDING;
            // Monto pendiente igual al monto total de la cuota
            assert installment.getOutstandingAmount().compareTo(expectedAmount) == 0;
            // Referencia correcta a préstamo
            assert installment.getLoan() == loan;
        }
    }

    @Test
    void save_generatesCorrectInstallments_biweeklyFrequency() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(UUID.randomUUID());
        loan.setTotalAmount(new BigDecimal("1400.00"));
        loan.setPurchaseDate(LocalDateTime.of(2023, 3, 1, 0, 0));

        PaymentSchema paymentSchema = new PaymentSchema();
        paymentSchema.setPaymentNumbers(7);
        paymentSchema.setFrequency("BIWEEKLY");

        // Act
        service.save(loan, paymentSchema);

        // Assert
        verify(repository, times(7)).save(any(LoanInstallment.class));

        ArgumentCaptor<LoanInstallment> captor = ArgumentCaptor.forClass(LoanInstallment.class);
        verify(repository, times(7)).save(captor.capture());

        LocalDate dueDateExpected = loan.getPurchaseDate().toLocalDate();
        BigDecimal expectedAmount = new BigDecimal("200.00"); // 1400/7 = 200.00 aprox

        for (LoanInstallment installment : captor.getAllValues()) {
            dueDateExpected = dueDateExpected.plusDays(14);
            assert installment.getAmount().compareTo(expectedAmount) == 0;
            assert installment.getDueDate().equals(dueDateExpected);
            assert installment.getStatus() == InstallmentStatus.PENDING;
            assert installment.getOutstandingAmount().compareTo(expectedAmount) == 0;
            assert installment.getLoan() == loan;
        }
    }

    @Test
    void save_generatesCorrectInstallments_unknownFrequency_defaultsToBiweekly() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(UUID.randomUUID());
        loan.setTotalAmount(new BigDecimal("600.00"));
        loan.setPurchaseDate(LocalDateTime.of(2023, 5, 20, 0, 0));
        PaymentSchema paymentSchema = new PaymentSchema();
        paymentSchema.setPaymentNumbers(3);
        paymentSchema.setFrequency("WEEKLY"); // frecuencia no conocida

        // Act
        service.save(loan, paymentSchema);

        // Assert
        verify(repository, times(3)).save(any(LoanInstallment.class));

        ArgumentCaptor<LoanInstallment> captor = ArgumentCaptor.forClass(LoanInstallment.class);
        verify(repository, times(3)).save(captor.capture());

        LocalDate dueDateExpected = loan.getPurchaseDate().toLocalDate();
        BigDecimal expectedAmount = new BigDecimal("200.00"); // 600/3

        for (LoanInstallment installment : captor.getAllValues()) {
            dueDateExpected = dueDateExpected.plusDays(14); // default biweekly
            assert installment.getAmount().compareTo(expectedAmount) == 0;
            assert installment.getDueDate().equals(dueDateExpected);
            assert installment.getStatus() == InstallmentStatus.PENDING;
            assert installment.getOutstandingAmount().compareTo(expectedAmount) == 0;
            assert installment.getLoan() == loan;
        }
    }
}
