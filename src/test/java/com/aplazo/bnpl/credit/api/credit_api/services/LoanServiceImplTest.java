package com.aplazo.bnpl.credit.api.credit_api.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.aplazo.bnpl.credit.api.credit_api.dto.LoanRequestDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.*;
import com.aplazo.bnpl.credit.api.credit_api.exception.PurchaseException;
import com.aplazo.bnpl.credit.api.credit_api.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class LoanServiceImplTest {

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private CustomerService customerService;

    @Mock
    private PaymentSchemaService paymentSchemaService;

    @Mock
    private LoanInstallmentService loanInstallmentService;

    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_successfulLoanCreation() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        LoanRequestDTO loanDTO = new LoanRequestDTO();
        loanDTO.setCustomerId(customerId);
        loanDTO.setAmount(new BigDecimal("500"));

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Juan Pérez");
        customer.setAvailable_credit(new BigDecimal("1000"));

        PaymentSchema paymentSchema = new PaymentSchema();
        paymentSchema.setOrderId(1);
        paymentSchema.setInterestRate(new BigDecimal("0.1"));
        paymentSchema.setDescription("Test schema");
        paymentSchema.setPaymentNumbers(5);
        paymentSchema.setFrequency("MONTHLY");

        Loan savedLoan = new Loan();
        savedLoan.setId(UUID.randomUUID());

        when(customerService.findById(customerId)).thenReturn(Optional.of(customer));
        when(paymentSchemaService.get(customer)).thenReturn(Optional.of(paymentSchema));
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Act
        Loan result = loanService.save(loanDTO);

        // Assert
        assertNotNull(result);
        assertEquals(savedLoan.getId(), result.getId());
        assertEquals(PurchaseStatus.APPROVED, result.getStatus());
        assertEquals(customerId, result.getIdClient());
        assertTrue(result.getTotalAmount().compareTo(loanDTO.getAmount()) > 0); // total > amount due commission
        verify(customerService).update(eq(customerId), any(Customer.class));
        verify(loanInstallmentService).save(eq(result), eq(paymentSchema));
    }

    @Test
    void save_throwsExceptionWhenCustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        LoanRequestDTO loanDTO = new LoanRequestDTO();
        loanDTO.setCustomerId(customerId);
        loanDTO.setAmount(new BigDecimal("500"));

        when(customerService.findById(customerId)).thenReturn(Optional.empty());

        PurchaseException thrown = assertThrows(PurchaseException.class, () -> loanService.save(loanDTO));
        assertTrue(thrown.getMessage().contains("Cliente con ID"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void save_throwsExceptionWhenCreditInsufficient() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        LoanRequestDTO loanDTO = new LoanRequestDTO();
        loanDTO.setCustomerId(customerId);
        loanDTO.setAmount(new BigDecimal("1500"));

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setAvailable_credit(new BigDecimal("1000"));

        when(customerService.findById(customerId)).thenReturn(Optional.of(customer));

        // Act & Assert
        PurchaseException thrown = assertThrows(PurchaseException.class, () -> loanService.save(loanDTO));
        assertTrue(thrown.getMessage().contains("Crédito insuficiente"));
        verify(loanRepository).save(argThat(loan -> loan.getStatus() == PurchaseStatus.REJECTED));
    }

    @Test
    void save_throwsExceptionWhenPaymentSchemaNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        LoanRequestDTO loanDTO = new LoanRequestDTO();
        loanDTO.setCustomerId(customerId);
        loanDTO.setAmount(new BigDecimal("500"));

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setAvailable_credit(new BigDecimal("1000"));

        when(customerService.findById(customerId)).thenReturn(Optional.of(customer));
        when(paymentSchemaService.get(customer)).thenReturn(Optional.empty());

        // Act & Assert
        PurchaseException thrown = assertThrows(PurchaseException.class, () -> loanService.save(loanDTO));
        assertTrue(thrown.getMessage().contains("No se encontró un esquema de pago"));
        verify(loanRepository).save(argThat(loan -> loan.getStatus() == PurchaseStatus.REJECTED));
    }
}
