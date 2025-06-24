package com.aplazo.bnpl.credit.api.credit_api.services;

import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;
import com.aplazo.bnpl.credit.api.credit_api.repositories.CreditLineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditLineImplTest {

    @Mock
    private CreditLineRepository repository;

    @InjectMocks
    private CreditLineImpl service;

    @Test
    void testGet_ReturnsCreditLine_WhenFound() {
        int age = 30;
        CreditLine creditLine = new CreditLine();
        Optional<CreditLine> expected = Optional.of(creditLine);

        when(repository.findFirstByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndActiveTrue(age, age))
                .thenReturn(expected);

        Optional<CreditLine> result = service.get(age);

        assertTrue(result.isPresent());
        assertEquals(creditLine, result.get());
        verify(repository).findFirstByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndActiveTrue(age, age);
    }

    @Test
    void testGet_ReturnsEmpty_WhenNotFound() {
        int age = 99;
        when(repository.findFirstByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndActiveTrue(age, age))
                .thenReturn(Optional.empty());

        Optional<CreditLine> result = service.get(age);

        assertFalse(result.isPresent());
        verify(repository).findFirstByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndActiveTrue(age, age);
    }

    @Test
    void testDeleteAllRules_CallsDeleteAll() {
        service.deleteAllRules();
        verify(repository).deleteAll();
    }
}
