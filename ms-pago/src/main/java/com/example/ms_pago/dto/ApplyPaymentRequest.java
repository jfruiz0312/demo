package com.example.ms_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyPaymentRequest {
        private String customerId;
        private BigDecimal amount;
        private String paymentMethod;
        private String transactionReference;
}
