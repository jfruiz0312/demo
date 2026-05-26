package com.example.ms_apply_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyPaymentResponse {
    private String status;
    private String message;
    private String transactionReference;

}
