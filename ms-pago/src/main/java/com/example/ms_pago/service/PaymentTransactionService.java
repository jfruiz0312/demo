package com.example.ms_pago.service;

import com.example.ms_pago.dto.ApplyPaymentRequest;
import com.example.ms_pago.dto.ApplyPaymentResponse;
import com.example.ms_pago.dto.PaymentRequest;
import com.example.ms_pago.dto.PaymentResponse;
import com.example.ms_pago.entities.PaymentTransaction;
import com.example.ms_pago.exception.BusinessException;
import com.example.ms_pago.repository.CustomerRepository;
import com.example.ms_pago.repository.PaymentTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionService {

    private final CustomerRepository customerRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final WebClient webClient;

    @Value("${max-amount}")
    private BigDecimal maxAmount;

    @Value("${payment.service.base-url:http://localhost:8082}")
    private String executorUrl;


    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        String transactionReference = generateTransactionReference();
        log.info("Procesando pago - Referencia: {}, Cliente: {}, Monto: {}",
                transactionReference, request.getCustomerId(), request.getPaymentAmount());

        try {
            // 1. Validar cliente
            validateCustomer(String.valueOf(request.getCustomerId()));

            // 2. Validar monto
            validateAmount(BigDecimal.valueOf(request.getPaymentAmount()));

            // 3. Crear transacción inicial (PENDING)
            PaymentTransaction   transaction = createInitialTransaction(request, transactionReference);

            // 4. Llamar al servicio executor para aplicar el pago
            ApplyPaymentResponse executorResponse = callExecutorService(request, transactionReference);

            // 5. Actualizar transacción según resultado
            if ("SUCCESS".equalsIgnoreCase(executorResponse.getStatus())) {
                transaction.setStatus("COMPLETED");
                paymentTransactionRepository.save(transaction);
                log.info("Pago completado exitosamente - Referencia: {}", transactionReference);
                return new PaymentResponse("success", "Pago procesado con éxito", transactionReference);
            } else {
                transaction.setStatus("FAILED");
                transaction.setDescription(executorResponse.getMessage());
                paymentTransactionRepository.save(transaction);
                throw new BusinessException(executorResponse.getMessage());
            }

        } catch (Exception e) {
            log.error("Error procesando pago - Referencia: {}, Error: {}", transactionReference, e.getMessage());
            throw e;
        }
    }

    private ApplyPaymentResponse callExecutorService(PaymentRequest request, String transactionReference) {
        try {
            ApplyPaymentRequest applyPaymentRequest = new ApplyPaymentRequest();
            applyPaymentRequest.setCustomerId(String.valueOf(request.getCustomerId()));
            applyPaymentRequest.setAmount(BigDecimal.valueOf(request.getPaymentAmount()));
            applyPaymentRequest.setPaymentMethod(request.getPaymentMethod());
            applyPaymentRequest.setTransactionReference(transactionReference);

            return webClient.post()
                    .uri(executorUrl + "/api/payments/apply-payment")
                    .bodyValue(applyPaymentRequest)
                    .retrieve()
                    .bodyToMono(ApplyPaymentResponse.class)
                    .block();

        } catch (Exception e) {
            log.error("Error llamando al servicio executor: {}", e.getMessage());
            throw new BusinessException("Servicio de pagos no disponible. Intente más tarde.");
        }
    }

    private void validateCustomer(String customerId) {
        if (!customerRepository.existsByCustomerId(customerId)) {
            throw new BusinessException("Cliente no encontrado con ID: " + customerId);
        }
        log.info("Cliente validado exitosamente: {}", customerId);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a 0");
        }
        if (amount.compareTo(maxAmount) > 0) {
            throw new BusinessException("El monto excede el límite máximo permitido de " + maxAmount);
        }
        log.info("Monto validado exitosamente: {}", amount);
    }

    private PaymentTransaction createInitialTransaction(PaymentRequest request, String transactionReference) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionReference(transactionReference);
        transaction.setCustomerId(String.valueOf(request.getCustomerId()));
        transaction.setAmount(BigDecimal.valueOf(request.getPaymentAmount()));
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setStatus("PENDING");
        return paymentTransactionRepository.save(transaction);
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() +
                "-" + System.currentTimeMillis();
    }
}