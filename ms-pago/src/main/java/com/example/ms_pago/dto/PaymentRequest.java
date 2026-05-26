package com.example.ms_pago.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull(message = "Customer ID is required")
    @Min(value = 1, message = "Customer ID must be a positive number")
    private Long customerId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    @DecimalMax(value = "1000.00", message = "Payment amount must be less than or equal to 10,000")
    private Double paymentAmount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(credit_card|debit_card|paypal)$", message = "Payment method must be either 'credit_card', 'debit_card', or 'paypal'")
    private String paymentMethod;

    @Email(message = "Customer email must be a valid email address")
    private String customerEmail;

    private String description;
    private String transactionReference;
}
