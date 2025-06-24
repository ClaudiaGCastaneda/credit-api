package com.aplazo.bnpl.credit.api.credit_api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase de utilidad para calcular montos de comisión.
 */
public class CommissionCalculator {

    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    private CommissionCalculator() { // para evitar que se instancie

    }

    /**
     * Calcula el monto de la comisión basado en el monto de la compra y la tasa de
     * interés.
     * El resultado se redondea a la escala y modo de redondeo por defecto (2
     * decimales, HALF_UP).
     *
     * @param purchaseAmount El monto total de la compra.
     * @param interestRate   La tasa de interés (ej. 0.13 para 13%).
     * @return El monto de la comisión calculado y redondeado.
     * @throws IllegalArgumentException si alguno de los BigDecimal de entrada es
     *                                  nulo.
     */
    public static BigDecimal calculateCommission(BigDecimal purchaseAmount, BigDecimal interestRate) {
        if (purchaseAmount == null || interestRate == null) {
            throw new IllegalArgumentException(
                    "El monto de la compra y la tasa de interés no pueden ser nulos para calcular la comisión.");
        }

        BigDecimal commission = purchaseAmount.multiply(interestRate);

        return commission.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

}
