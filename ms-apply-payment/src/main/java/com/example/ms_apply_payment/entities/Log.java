package com.example.ms_apply_payment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed from Log to Long

    private String status;
    private String message;
    private String transactionReference;

    public Log(String status, String message, String transactionReference) {
        this.status = status;
        this.message = message;
        this.transactionReference = transactionReference;
    }
}