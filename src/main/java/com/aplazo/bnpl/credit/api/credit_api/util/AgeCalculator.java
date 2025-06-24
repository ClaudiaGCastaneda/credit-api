package com.aplazo.bnpl.credit.api.credit_api.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

@Component
public class AgeCalculator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Calcula la edad de una persona basándose en su fecha de nacimiento en formato
     * String.
     *
     * @param birthDateString La fecha de nacimiento en formato "yyyy/MM/dd".
     * @return La edad actual en años.
     * @throws IllegalArgumentException Si el formato de la fecha de nacimiento es
     *                                  inválido
     *                                  o si la fecha de nacimiento es nula o vacía.
     */
    public int calculateAge(String birthDateString) {
        if (birthDateString == null || birthDateString.isEmpty()) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede estar vacía.");
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateString, DATE_FORMATTER);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Formato de fecha de nacimiento inválido. Se espera yyyy/MM/dd. Error: " + e.getMessage());
        }
    }
}