package com.example.ms_pago.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
public class PaymentTransaction {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "transaction_reference", unique = true, nullable = false)
        private String transactionReference;

        @Column(name = "customer_id", nullable = false)
        private String customerId;

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal amount;

        @Column(name = "payment_method", nullable = false)
        private String paymentMethod;


        @Column(nullable = false)
        private String status;

        private String Description;
        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
}
