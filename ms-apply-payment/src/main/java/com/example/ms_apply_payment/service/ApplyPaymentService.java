package com.example.ms_apply_payment.service;

import com.example.ms_apply_payment.dto.ApplyPaymentRequest;
import com.example.ms_apply_payment.dto.ApplyPaymentResponse;
import com.example.ms_apply_payment.entities.Log;
import com.example.ms_apply_payment.repository.LogRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j

public class ApplyPaymentService {

    private  final LogRespository logRespository;


    // Simulación de base de datos de clientes con saldos
    private final Map<String, BigDecimal> customerBalances = new HashMap<>();

    public ApplyPaymentService(LogRespository logRespository) {
        this.logRespository = logRespository;

        // Inicializar saldos de ejemplo
        customerBalances.put("12345", new BigDecimal("5000.00"));
        customerBalances.put("12346", new BigDecimal("2500.00"));
        customerBalances.put("12347", new BigDecimal("10000.00"));
    }

    // Método para aplicar el pago
    public ApplyPaymentResponse applyPayment(ApplyPaymentRequest request) {
        log.info("Applying payment for customerId: {}, amount: {}, paymentMethod: {}, transactionReference: {}",
               request.getCustomerId(), request.getAmount(), request.getPaymentMethod(), request.getTransactionReference());

        // Validar método de pago
        if (!isValidPaymentMethod(request.getPaymentMethod())) {
            log.warn("Invalid payment method: {}", request.getPaymentMethod());
            logRespository.save(new Log(null, "error", "Invalid payment method: " + request.getPaymentMethod(), request.getTransactionReference()));            return new ApplyPaymentResponse("error", "Invalid payment method", request.getTransactionReference());
        }

        // Validar saldo suficiente
        if (!hasSufficientBalance( request.getCustomerId(), request.getAmount())) {
            log.warn("Insufficient balance for customerId: {}", request.getCustomerId());
            logRespository.save(new Log(null, "error", "Insufficient balance for customerId: " + request.getPaymentMethod(), request.getTransactionReference()));
            return new ApplyPaymentResponse("error", "Insufficient balance", request.getTransactionReference());
        }

        // Deduct balance
        deductBalance(request.getCustomerId(), request.getAmount());
        log.info("Payment applied successfully for customerId: {}, new balance: {}", request.getCustomerId(), customerBalances.get(request.getCustomerId()));
        logRespository.save(new Log(null, "success", "Payment applied successfully: " + request.getPaymentMethod(), request.getTransactionReference()));
        return new ApplyPaymentResponse("success", "Payment applied successfully", request.getTransactionReference());
    }

    //Validate paymentMethod
    private boolean isValidPaymentMethod(String paymentMethod) {
        return "credit_card".equalsIgnoreCase(paymentMethod) ||
               "debit_card".equalsIgnoreCase(paymentMethod) ||
               "bank_transfer".equalsIgnoreCase(paymentMethod);
    }
    //Validate SufficentBalance
    public boolean hasSufficientBalance(String customerId, BigDecimal amount) {
        BigDecimal balance = customerBalances.get(customerId);
        return balance != null && balance.compareTo(amount) >= 0;
    }
    //Validate deduct balance

    public void deductBalance(String customerId, BigDecimal amount) {
        BigDecimal balance = customerBalances.get(customerId);
        if (balance != null) {
            customerBalances.put(customerId, balance.subtract(amount));
        }
    }
}
