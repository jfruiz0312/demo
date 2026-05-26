package com.example.ms_apply_payment.controller;

import com.example.ms_apply_payment.dto.ApplyPaymentRequest;
import com.example.ms_apply_payment.dto.ApplyPaymentResponse;
import com.example.ms_apply_payment.service.ApplyPaymentService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class ApplyPaymentController {


    private final ApplyPaymentService applyPaymentService;

    //endpoint healt check

    @GetMapping("/health")
    public String healthCheck() {
        return "Apply Payment Service is up and running!";
    }
    @PostMapping("/apply-payment")
    public ApplyPaymentResponse applyPayment(@RequestBody ApplyPaymentRequest request) {
        // Lógica para procesar el pago
        // Aquí puedes llamar a otros servicios, validar la solicitud, etc.
        log.info("Received apply payment request for customerId: {}, amount: {}, paymentMethod: {}, transactionReference: {}",
                request.getCustomerId(), request.getAmount(), request.getPaymentMethod(), request.getTransactionReference());

       ApplyPaymentResponse response= applyPaymentService.applyPayment(request);
       log.info("Response ", response.getStatus());
        // Simulación de respuesta exitosa
        return new ApplyPaymentResponse("success", "Payment applied successfully", response.getTransactionReference());

    }
}
