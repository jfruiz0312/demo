package com.example.ms_apply_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ApplyPaymentRequest {
        private String customerId;
        private BigDecimal amount;
        private String paymentMethod;
        private String transactionReference;
}
