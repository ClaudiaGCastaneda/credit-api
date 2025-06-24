package com.aplazo.bnpl.credit.api.credit_api.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.entities.PaymentSchema;
import com.aplazo.bnpl.credit.api.credit_api.repositories.PaymentSchemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class PaymentSchemaServiceImplTest {

    @InjectMocks
    private PaymentSchemaServiceImpl paymentSchemaService;

    @Mock
    private PaymentSchemaRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get_returnsSchemaOne_whenInitialIsC_L_or_H() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("Carlos"); // inicial 'C'

        PaymentSchema schema1 = new PaymentSchema();
        schema1.setDescription("Schema 1");

        when(repository.findByDescription("Schema 1")).thenReturn(Optional.of(schema1));

        // Act
        Optional<PaymentSchema> result = paymentSchemaService.get(customer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Schema 1", result.get().getDescription());
    }

    @Test
    void get_returnsSchemaTwo_whenInitialIsNotC_L_or_H() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("Maria"); // inicial 'M', no está en SCHEMA_ONE_INITIALS

        PaymentSchema schema2 = new PaymentSchema();
        schema2.setDescription("Schema 2");

        when(repository.findByDescription("Schema 2")).thenReturn(Optional.of(schema2));

        // Act
        Optional<PaymentSchema> result = paymentSchemaService.get(customer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Schema 2", result.get().getDescription());
    }

    @Test
    void get_returnsEmptyOptional_whenNoSchemaFound() {
        // Arrange
        Customer customer = new Customer();
        customer.setName("Lucas"); // inicial 'L', debería buscar Schema 1

        when(repository.findByDescription("Schema 1")).thenReturn(Optional.empty());

        // Act
        Optional<PaymentSchema> result = paymentSchemaService.get(customer);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteAllRules_invokesRepositoryDeleteAll() {
        // Act
        paymentSchemaService.deleteAllRules();

        // Assert
        verify(repository, times(1)).deleteAll();
    }
}
