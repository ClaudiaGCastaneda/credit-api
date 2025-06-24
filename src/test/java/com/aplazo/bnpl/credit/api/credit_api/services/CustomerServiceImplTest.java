package com.aplazo.bnpl.credit.api.credit_api.services;

import com.aplazo.bnpl.credit.api.credit_api.dto.ClientRegistrationDTO;
import com.aplazo.bnpl.credit.api.credit_api.dto.ClientResponseDTO;
import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;
import com.aplazo.bnpl.credit.api.credit_api.entities.Customer;
import com.aplazo.bnpl.credit.api.credit_api.repositories.CustomerRepository;
import com.aplazo.bnpl.credit.api.credit_api.util.AgeCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CreditLineService creditLineService;

    @Mock
    private AgeCalculator ageCalculator;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setup() throws Exception {
        setPrivateField("minAge", 18);
        setPrivateField("maxAge", 65);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = CustomerServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(customerService, value);
    }

    @Test
    void testSave_ValidClient_SavesCustomerAndReturnsDTO() {
        ClientRegistrationDTO dto = new ClientRegistrationDTO();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setSecondLastName("García");
        dto.setDateOfBirth("1990/05/15"); // Formato yyyy/MM/dd

        int calculatedAge = 34;
        when(ageCalculator.calculateAge(dto.getDateOfBirth())).thenReturn(calculatedAge);

        CreditLine creditLine = new CreditLine();
        creditLine.setId(1); // int id
        creditLine.setCreditLine(new BigDecimal("10000.00"));

        when(creditLineService.get(calculatedAge)).thenReturn(Optional.of(creditLine));

        Customer savedCustomer = new Customer();
        savedCustomer.setId(UUID.randomUUID());
        savedCustomer.setAvailable_credit(new BigDecimal(10000.0));

        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        // Act
        ClientResponseDTO response = customerService.save(dto);

        // Assert
        assertNotNull(response);
        assertEquals(savedCustomer.getId(), response.getId());
        assertEquals(savedCustomer.getAvailable_credit(), response.getAssignedCreditAmount());

        verify(repository).save(any(Customer.class));
    }

    @Test
    void testSave_InvalidAge_ThrowsException() {
        ClientRegistrationDTO dto = new ClientRegistrationDTO();
        dto.setDateOfBirth("2015/01/01"); // menor de edad (formato correcto)

        when(ageCalculator.calculateAge(dto.getDateOfBirth())).thenReturn(10); // menor que minAge

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            customerService.save(dto);
        });

        assertTrue(thrown.getMessage().contains("No es posible otorgarle una línea de crédito"));
    }

    @Test
    void testSave_NoCreditLineAvailable_DoesNotSaveCustomer() {
        ClientRegistrationDTO dto = new ClientRegistrationDTO();
        dto.setFirstName("Ana");
        dto.setLastName("Martínez");
        dto.setSecondLastName("López");
        dto.setDateOfBirth("1985/05/05"); // Formato correcto

        int calculatedAge = 39;
        when(ageCalculator.calculateAge(dto.getDateOfBirth())).thenReturn(calculatedAge);

        when(creditLineService.get(calculatedAge)).thenReturn(Optional.empty());

        ClientResponseDTO response = customerService.save(dto);
        assertNull(response);
        verify(repository, never()).save(any());
    }

}
